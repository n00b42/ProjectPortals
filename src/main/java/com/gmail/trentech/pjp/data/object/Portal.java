package com.gmail.trentech.pjp.data.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.DataQueries;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.Serializer;

public class Portal extends PortalBase {

	private static ConcurrentHashMap<String, Portal> cache = new ConcurrentHashMap<>();

	private final List<Location<World>> frame;
	private final List<Location<World>> fill;
	private Particle particle = Particles.getDefaultEffect("portal");
	private Optional<ParticleColor> color = Particles.getDefaultColor("portal", particle.isColorable());
	
	public Portal(String destination, Rotation rotation, List<Location<World>> frame, List<Location<World>> fill, Particle particle, Optional<ParticleColor> color, double price, boolean bungee) {
		super(destination, rotation, price, bungee);

		this.frame = frame;
		this.fill = fill;

		if(this.particle != null) {
			this.particle = particle;
		}
		
		if(this.color != null) {
			this.color = color;
		}
	}

	public Portal(String name, String destination, Rotation rotation, List<Location<World>> frame, List<Location<World>> fill, Particle particle, Optional<ParticleColor> color, double price, boolean bungee) {
		super(name, destination, rotation, price, bungee);

		this.frame = frame;
		this.fill = fill;

		if(this.particle != null) {
			this.particle = particle;
		}
		
		if(this.color != null) {
			this.color = color;
		}
		
		this.color = color;
	}
	
	public Particle getParticle() {
		return particle;
	}
	
	public void setParticle(Particle particle) {
		this.particle = particle;
	}
	
	public Optional<ParticleColor> getParticleColor() {
		return color;
	}
	
	public void setParticleColor(ParticleColor color) {
		this.color = Optional.of(color);
	}
	
	public List<Location<World>> getFrame() {
		return frame;
	}
	
	public List<Location<World>> getFill() {
		return fill;
	}

	public static Optional<Portal> get(String name) {
		if(cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}
		
		return Optional.empty();
	}

	public static Optional<Portal> get(Location<World> location) {
		for(Entry<String, Portal> entry : cache.entrySet()) {
			Portal portal = entry.getValue();

			List<Location<World>> frame = portal.getFrame();
			
			if(!frame.get(0).getExtent().equals(location.getExtent())) {
				continue;
			}

			for(Location<World> loc : frame) {
				if(loc.getBlockPosition().equals(location.getBlockPosition())) {
					return Optional.of(portal);
				}
			}
			
			for(Location<World> loc : portal.getFill()) {
				if(loc.getBlockPosition().equals(location.getBlockPosition())) {
					return Optional.of(portal);
				}
			}
		}
		
		return Optional.empty();
	}
	
	public static ConcurrentHashMap<String, Portal> all(){
		return cache;
	}

	public void create() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Portals (Name, Portal) VALUES (?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, Serializer.serialize(this));

			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);

			particle.createTask(name, this.getFill(), getParticleColor());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE Portals SET Portal = ? WHERE Name = ?");

			statement.setString(1, Serializer.serialize(this));
			statement.setString(2, name);
			
			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
			
			for(Task task : Main.getGame().getScheduler().getScheduledTasks()) {
				if(task.getName().equals(name)) {
					task.cancel();
				}
			}

			particle.createTask(name, this.getFill(), getParticleColor());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DELETE from Portals WHERE Name = ?");
		    
			statement.setString(1, name);
			statement.executeUpdate();
			
			connection.close();
			
			cache.remove(name);
			
			for(Task task : Main.getGame().getScheduler().getScheduledTasks()) {
				if(task.getName().equals(name)) {
					task.cancel();
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Portals");
		    
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String name = result.getString("Name");
				
				Portal portal;
				try{
					portal = Serializer.deserializePortal(result.getString("Portal"));
					portal.setName(name);
				}catch(Exception e) {
					Main.getLog().error("Could not deserialize Portal: " + name);
					portal = new Portal(name, Main.getGame().getServer().getDefaultWorldName() + ":spawn", Rotation.EAST, new ArrayList<Location<World>>(), new ArrayList<Location<World>>(), null, null, 0, false);
				}

				cache.put(name, portal);

	    		portal.getParticle().createTask(name, portal.getFill(), portal.getParticleColor());
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer();
		
		container.set(DataQueries.DESTINATION, destination).set(DataQueries.ROTATION, rotation.getName())
		.set(DataQueries.PRICE, price).set(DataQueries.BUNGEE, bungee).set(DataQueries.PARTICLE, particle.getName());
		
		List<String> frame = new ArrayList<>();
		
		for(Location<World> location : this.frame) {
			frame.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
		container.set(DataQueries.FRAME, frame);
		
		List<String> fill = new ArrayList<>();

		for(Location<World> location : this.fill) {
			fill.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
		container.set(DataQueries.FILL, fill);
		
		if(color.isPresent()) {
			container.set(DataQueries.COLOR, color.get().getName());
		}else {
			container.set(DataQueries.COLOR, "NONE");
		}
		
        return container;
    }
}
