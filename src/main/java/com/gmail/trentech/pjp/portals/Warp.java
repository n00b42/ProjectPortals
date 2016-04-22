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

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Utils;

public class Warp extends SQLUtils{

	private final String name;
	public String destination;
	private Rotation rotation;
	private double price;

	private static ConcurrentHashMap<String, Warp> cache = new ConcurrentHashMap<>();
	
	public Warp(String name, String destination, Rotation rotation, double price) {
		this.name = name;
		this.destination = destination;
		this.rotation = rotation;
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
		    PreparedStatement statement = connection.prepareStatement("UPDATE Warps SET Price = ? WHERE Name = ?");

		    statement.setDouble(1, price);
			statement.setString(2, this.name);
			
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
		
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE Warps SET Rotation = ? WHERE Name = ?");

		    statement.setString(1, rotation.getName());
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

	public void setDestination(String destination){
		this.destination = destination;
		
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE Warps SET Destination = ? WHERE Name = ?");

		    statement.setString(1, destination);
			statement.setString(2, this.name);
			
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Optional<Warp> get(String name){
		Optional<Warp> optional = Optional.empty();
		
		if(cache.containsKey(name)){
			optional = Optional.of(cache.get(name));
		}
		
		return optional;
	}
	
	public static void remove(String name){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DELETE from Warps WHERE Name = ?");
		    
			statement.setString(1, name);
			statement.executeUpdate();
			
			connection.close();
			
			cache.remove(name);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void save(){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Warps (Name, Destination, Price) VALUES (?, ?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, destination);
		    statement.setDouble(3, price);

			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Warp> list(){
		List<Warp> list = new ArrayList<>();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Warps");
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {				
				String name = result.getString("Name");
		    	String destination = result.getString("Destination");
		    	String rotation = result.getString("Rotation");
		    	
		    	if(rotation == null){
		    		rotation = Rotation.EAST.getName();
		    	}

	    		double price = result.getDouble("Price");

				list.add(new Warp(name, destination, Rotation.get(rotation).get(), price));
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void init(){
		update();
		
		for(Warp warp : Warp.list()){
			Rotation rotation = warp.getRotation();
			String[] args = warp.destination.split(":");
			
			if(args.length == 3){
				rotation = Rotation.get(args[2]).get();
				warp.setDestination(args[0] + ":" + args[1]);
			}
			
			warp.setRotation(rotation);
			warp.setPrice(warp.getPrice());
			
			cache.put(warp.getName(), warp);
		}
	}
	
	public static void update(){
		try {
			Connection connection = getDataSource().getConnection();
			
		    PreparedStatement statement = connection.prepareStatement("ALTER TABLE Warps ADD IF NOT EXISTS Rotation TEXT");
		    statement.executeUpdate();
		    
		    statement = connection.prepareStatement("ALTER TABLE Warps ADD IF NOT EXISTS Price DOUBLE");
		    statement.executeUpdate();
		    
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	  
	}
}
