package com.gmail.trentech.pjp.portals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Utils;

public class Portal extends SQLUtils {

	private final String name;
	public final String destination;
	private final List<String> frame;
	private final List<String> fill;
	private String particle;
	
	private static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

	public Portal(String name, String destination, List<String> frame, List<String> fill, String particle) {
		this.name = name;
		this.destination = destination;
		this.frame = frame;
		this.fill = fill;
		
		this.particle = particle;
		if(this.particle == null){
			this.particle = new ConfigManager().getConfig().getNode("options", "particles", "type", "portal").getString().toUpperCase();
		}
	}
	
	public Portal(String name, String destination, List<Location<World>> frame, List<Location<World>> fill, String particle, String dummy) {
		this.name = name;
		this.destination = destination;
		
		this.frame = new ArrayList<>();
		this.fill = new ArrayList<>();
		
		for(Location<World> location : frame){
			String loc = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
			cache.put(loc, name);
			this.frame.add(loc);
		}
		for(Location<World> location : fill){
			String loc = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
			cache.put(loc, name);
			this.fill.add(loc);
		}
		
		this.particle = particle;
		if(this.particle == null){
			this.particle = new ConfigManager().getConfig().getNode("options", "particles", "type", "portal").getString().toUpperCase();
		}
	}

	public String getName() {
		return name;
	}
	
	public String getParticle(){
		return particle;
	}

	public void setParticle(String particle){
		this.particle = particle;
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE Portals SET Particle = ? WHERE Name = ?");

		    statement.setString(1, particle);
			statement.setString(2, this.name);
			
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(Task task : Main.getGame().getScheduler().getScheduledTasks()){
			if(task.getName().equals(this.name)){
				task.cancel();
			}
		}

		String[] split = particle.split(":");
		if(split.length == 2){
			Particles.get(split[0]).get().createTask(getName(), getFill(), ParticleColor.get(split[1]).get());			
		}else{
			Particles.get(split[0]).get().createTask(getName(), getFill());
		}
	}
	
	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		Optional<World> optional = Main.getGame().getServer().getWorld(args[0]);
		
		if(!optional.isPresent()){
			return Optional.empty();
		}
		World world = optional.get();
		
		if(args[1].equalsIgnoreCase("random")){
			return Optional.of(Utils.getRandomLocation(world));
		}else if(args[1].equalsIgnoreCase("spawn")){
			return Optional.of(world.getSpawnLocation());
		}else{
			String[] coords = args[1].split("\\.");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			return Optional.of(world.getLocation(x, y, z));	
		}
	}

	public Optional<Vector3d> getRotation(){
		String[] args = destination.split(":");
		
		if(args.length != 3){
			return Optional.empty();
		}
		
		Optional<Rotation> optional = Rotation.get(args[2]);
		
		if(!optional.isPresent()){
			return Optional.empty();
		}
		
		return Optional.of(new Vector3d(0,optional.get().getValue(),0));
	}
	
	public List<Location<World>> getFrame() {
		List<Location<World>> list = new ArrayList<>();
		
		for(String loc : frame){
			String[] args = loc.split(":");
			
			if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(args[0]).get();

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
		
		for(String loc : fill){
			String[] args = loc.split(":");
			
			if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(args[0]).get();

			String[] coords = args[1].split("\\.");

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			list.add(world.getLocation(x, y, z));	
		}
		return list;
	}

	public static Optional<Portal> get(Location<World> location){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		Optional<Portal> optionalPortal = Optional.empty();
		
		if(cache.containsKey(name)){
			try {
			    Connection connection = getDataSource().getConnection();
			    
			    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Portals");
			    
				ResultSet result = statement.executeQuery();
				
				while (result.next()) {
					
					String[] frameArray = result.getString("Frame").split(";");
					List<String> frame = new ArrayList<String>(Arrays.asList(frameArray));
					
					String[] fillArray = result.getString("Fill").split(";");
					List<String> fill = new ArrayList<String>(Arrays.asList(fillArray));

			    	if(!frame.contains(name) && !fill.contains(name)){
			    		continue;
			    	}
			    	
			    	String portalName = result.getString("Name");
			    	String destination = result.getString("Destination");
			    	String particle = result.getString("Particle");
			    	
			    	optionalPortal = Optional.of(new Portal(portalName, destination, frame, fill, particle));
					
					break;
				}
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return optionalPortal;
	}

	public static Optional<Portal> getByName(String name){
		Optional<Portal> optionalPortal = Optional.empty();
		
		for(Entry<String, String> entry : cache.entrySet()){
			if(!entry.getValue().equalsIgnoreCase(name)){
				continue;
			}
			
			try {
			    Connection connection = getDataSource().getConnection();
			    
			    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Portals");
			    
				ResultSet result = statement.executeQuery();
				
				while (result.next()) {
					if (result.getString("Name").equalsIgnoreCase(name)) {

						String[] frameArray = result.getString("Frame").split(";");
						List<String> frame = new ArrayList<String>(Arrays.asList(frameArray));
						
						String[] fillArray = result.getString("Fill").split(";");
						List<String> fill = new ArrayList<String>(Arrays.asList(fillArray));
						
						String destination = result.getString("Destination");
						String particle = result.getString("Particle");
						
						optionalPortal = Optional.of(new Portal(name, destination, frame, fill, particle));
						
						break;
					}
				}
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			break;
		}
		return optionalPortal;
	}
	
	public static void remove(String name){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DELETE from Portals WHERE Name = ?");
		    
			statement.setString(1, name);
			statement.executeUpdate();
			
			connection.close();
			
			for(Task task : Main.getGame().getScheduler().getScheduledTasks()){
				if(task.getName().equals(name)){
					task.cancel();
				}
			}
			
			for(Entry<String, String> entry : cache.entrySet()){
				if(entry.getValue().equalsIgnoreCase(name)){
					cache.remove(entry.getKey());
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(Portal portal){	
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Portals (Name, Frame, Fill, Destination, Particle) VALUES (?, ?, ?, ?, ?)");	
			
		    statement.setString(1, portal.name);
		    
		    StringBuilder stringBuilder = new StringBuilder();
		    for (String string : portal.frame){
		    	stringBuilder.append(string + ";");
		    }
		    statement.setString(2, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    
		    stringBuilder = new StringBuilder();
		    for (String string : portal.fill){
		    	stringBuilder.append(string + ";");
		    }	    
		    statement.setString(3, stringBuilder.toString().substring(0, stringBuilder.length() - 1));

		    statement.setString(4, portal.destination);
		    statement.setString(5, portal.particle);
		    
			statement.executeUpdate();
			
			connection.close();

			String[] split = portal.getParticle().split(":");
			if(split.length == 2){
				Particles.get(split[0]).get().createTask(portal.getName(), portal.getFill(), ParticleColor.get(split[1]).get());			
			}else{
				Particles.get(split[0]).get().createTask(portal.getName(), portal.getFill());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Portal> list(){
		List<Portal> list = new ArrayList<>();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Portals");
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				String name = result.getString("Name");

				String[] frameArray = result.getString("Frame").split(";");
				List<String> frame = new ArrayList<String>(Arrays.asList(frameArray));
				
				String[] fillArray = result.getString("Fill").split(";");
				List<String> fill = new ArrayList<String>(Arrays.asList(fillArray));
		    	
		    	String destination = result.getString("Destination");
		    	String particle = result.getString("Particle");
		    	
		    	list.add(new Portal(name, destination, frame, fill, particle));
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void init(){
		for(Portal portal : Portal.list()){
			if(!portal.getName().equals(portal.getName().toLowerCase())){
				portal.fixName();
			}
			String name = portal.getName().toLowerCase();

			for(String loc : portal.fill){
				cache.put(loc, name);
			}
			for(String loc : portal.frame){
				cache.put(loc, name);
			}
			
    		String[] split = portal.getParticle().split(":");
    		if(split.length == 2){
    			Particles.get(split[0]).get().createTask(name, portal.getFill(), ParticleColor.get(split[1]).get());
    		}else{
    			Particles.get(split[0]).get().createTask(name, portal.getFill());
    		}
		}
	}
	
	private void fixName(){
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE Portals SET Name = ? WHERE Name = ?");

		    statement.setString(1, this.name.toLowerCase());
			statement.setString(2, this.name);
			
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
