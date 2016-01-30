package com.gmail.trentech.pjp.portals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Utils;

public class Portal extends SQLUtils{

	private final String name;
	public final String destination;
	private final List<String> frame;
	private final List<String> fill;

	public Portal(String uuid, String destination, List<String> frame, List<String> fill) {
		this.name = uuid;
		this.destination = destination;
		this.frame = frame;
		this.fill = fill;
	}
	
	public Portal(String uuid, String destination, List<Location<World>> frame, List<Location<World>> fill, String dummy) {
		this.name = uuid;
		this.destination = destination;
		
		this.frame = new ArrayList<>();
		this.fill = new ArrayList<>();
		
		for(Location<World> location : frame){
			this.frame.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
		for(Location<World> location : fill){
			this.fill.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
	}

	public String getName() {
		return name;
	}

	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
			return Optional.empty();
		}
		World world = Main.getGame().getServer().getWorld(args[0]).get();
		
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
		Optional<Portal> optionalPortal = Optional.empty();
		
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
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
		    	
		    	String destination = result.getString("Destination");

		    	optionalPortal = Optional.of(new Portal(name, destination, frame, fill));
				
				break;
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return optionalPortal;
	}
	
	public static Optional<Portal> getByName(String name){
		Optional<Portal> optionalPortal = Optional.empty();

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
					
					optionalPortal = Optional.of(new Portal(name, destination, frame, fill));
					
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
				if(task.getName().contains(name)){
					task.cancel();
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(Portal portal){	
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Portals (Name, Frame, Fill, Destination) VALUES (?, ?, ?, ?)");	
			
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

			statement.executeUpdate();
			
			connection.close();
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

		    	list.add(new Portal(name, destination, frame, fill));
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
