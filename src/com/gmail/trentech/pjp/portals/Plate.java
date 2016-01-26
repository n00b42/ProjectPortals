package com.gmail.trentech.pjp.portals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Utils;

public class Plate extends SQLUtils{

	private final String name;
	private final String destination;

	public Plate(String name, String destination) {
		this.name = name;
		this.destination = destination;
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

	public static Optional<Plate> get(Location<World> location){
		Optional<Plate> optionalPlate = Optional.empty();
		
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Plates");
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("Name").equalsIgnoreCase(name)) {
					optionalPlate = Optional.of(new Plate(result.getString("Name"), result.getString("Destination")));
					
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return optionalPlate;
	}
	
	public static void remove(Location<World> location){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DELETE from Plates WHERE Name = ?");
		    
			statement.setString(1, name);
			statement.executeUpdate();
			
			connection.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(Location<World> location, String destination){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Plates (Name, Destination) VALUES (?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, destination);

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(String name, String destination){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Plates (Name, Destination) VALUES (?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, destination);

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
