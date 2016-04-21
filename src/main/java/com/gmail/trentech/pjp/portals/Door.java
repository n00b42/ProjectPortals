package com.gmail.trentech.pjp.portals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Utils;

public class Door extends SQLUtils{

	private final String name;
	private final String destination;
	private double price;
	
	private static ConcurrentHashMap<String, Door> cache = new ConcurrentHashMap<>();
	
	public Door(String name, String destination, double price) {
		this.name = name;
		this.destination = destination;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price){
		this.price = price;
		
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE Doors SET Price = ? WHERE Name = ?");

		    statement.setDouble(1, price);
			statement.setString(2, this.name);
			
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
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

	public static Optional<Door> get(Location<World> location){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		Optional<Door> optional = Optional.empty();
		
		if(cache.containsKey(name)){
			optional = Optional.of(cache.get(name));
		}
		
		return optional;
	}
	
	public static void remove(Location<World> location){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DELETE from Doors WHERE Name = ?");
		    
			statement.setString(1, name);
			statement.executeUpdate();
			
			connection.close();
			
			cache.remove(name);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(Location<World> location, String destination, double price){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Doors (Name, Destination, Price) VALUES (?, ?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, destination);
		    statement.setDouble(3, price);
		    
			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, new Door(name, destination, price));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(String name, String destination, double price){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Doors (Name, Destination, Price) VALUES (?, ?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, destination);
		    statement.setDouble(3, price);

			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, new Door(name, destination, price));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Door> list(){
		List<Door> list = new ArrayList<>();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Doors");
		    
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				list.add(new Door(result.getString("Name"), result.getString("Destination"), result.getDouble("Price")));
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void init(){
		for(Door door : Door.list()){
			cache.put(door.getName(), door);
		}
	}
}
