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
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Serializer;

public class Portal extends PortalBase {

	private static ConcurrentHashMap<String, Portal> cache = new ConcurrentHashMap<>();

	private final List<String> frame;
	private final List<String> fill;
	private String particle;
	
	public Portal(String destination, String rotation, List<String> frame, List<String> fill, String particle, double price) {
		super(destination, rotation, price);

		this.frame = frame;
		this.fill = fill;
		
		this.particle = particle;
		if(this.particle == null) {
			this.particle = new ConfigManager().getConfig().getNode("options", "particles", "type", "portal").getString().toUpperCase();
		}
	}

	public Portal(String name, String destination, String rotation, List<String> frame, List<String> fill, String particle, double price) {
		super(name, destination, rotation, price);

		this.frame = frame;
		this.fill = fill;
		
		this.particle = particle;
		if(this.particle == null) {
			this.particle = new ConfigManager().getConfig().getNode("options", "particles", "type", "portal").getString().toUpperCase();
		}
	}
	
	public String getParticle() {
		return particle;
	}
	
	public void setParticle(String particle) {
		this.particle = particle;
	}
	
	public List<Location<World>> getFrame() {
		List<Location<World>> list = new ArrayList<>();
		
		for(String loc : frame) {
			String[] args = loc.split(":");
			
			Optional<World> optionalWorld = Main.getGame().getServer().getWorld(args[0]);
			
			if(!optionalWorld.isPresent()) {
				continue;
			}
			World world = optionalWorld.get();

			String[] coords = args[1].split("\\.");

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			list.add(world.getLocation(x, y, z));	
		}
		return list;
	}
	
	public List<Location<World>> getFill() {
		List<Location<World>> list = new ArrayList<>();
		
		for(String loc : fill) {
			String[] args = loc.split(":");
			
			Optional<World> optionalWorld = Main.getGame().getServer().getWorld(args[0]);
			
			if(!optionalWorld.isPresent()) {
				continue;
			}
			World world = optionalWorld.get();

			String[] coords = args[1].split("\\.");

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			list.add(world.getLocation(x, y, z));	
		}
		return list;
	}

	public static Optional<Portal> get(String name) {
		if(cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}
		
		return Optional.empty();
	}

	public static Optional<Portal> get(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		for(Entry<String, Portal> entry : cache.entrySet()) {
			Portal portal = entry.getValue();
			
			for(Location<World> loc : portal.getFill()) {
				String fill = loc.getExtent().getName() + ":" + loc.getBlockX() + "." + loc.getBlockY() + "." + loc.getBlockZ();
				
				if(name.equals(fill)) {
					return Optional.of(portal);
				}
			}

			for(Location<World> loc : portal.getFrame()) {
				String frame = loc.getExtent().getName() + ":" + loc.getBlockX() + "." + loc.getBlockY() + "." + loc.getBlockZ();
				
				if(name.equals(frame)) {
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
			
			String[] split = this.getParticle().split(":");
			if(split.length == 2) {
				Particles.get(split[0]).get().createTask(name, this.getFill(), ParticleColor.get(split[1]).get());			
			}else{
				Particles.get(split[0]).get().createTask(name, this.getFill());
			}
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

			String[] split = this.getParticle().split(":");
			if(split.length == 2) {
				Particles.get(split[0]).get().createTask(name, this.getFill(), ParticleColor.get(split[1]).get());			
			}else{
				Particles.get(split[0]).get().createTask(name, this.getFill());
			}
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
				
				Portal portal = Serializer.deserializePortal(result.getString("Portal"));
				portal.setName(name);
				
				cache.put(name, portal);
				
	    		String[] split = portal.getParticle().split(":");
	    		if(split.length == 2) {
	    			Particles.get(split[0]).get().createTask(name, portal.getFill(), ParticleColor.get(split[1]).get());
	    		}else{
	    			Particles.get(split[0]).get().createTask(name, portal.getFill());
	    		}
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(DataQueries.DESTINATION, destination).set(DataQueries.ROTATION, rotation)
        		.set(DataQueries.FRAME, frame).set(DataQueries.FILL, fill).set(DataQueries.PARTICLE, particle).set(DataQueries.PRICE, price);
    }
}
