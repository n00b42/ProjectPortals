package com.gmail.trentech.pjp.data.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.utils.Serializer;

public class Door extends PortalBase {

	private static ConcurrentHashMap<String, Door> cache = new ConcurrentHashMap<>();
	
	public Door(String destination, String rotation, double price) {
		super(destination, rotation, price);
	}

	public static Optional<Door> get(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		if(cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}
		
		return Optional.empty();
	}

	public void create(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Doors (Name, Door) VALUES (?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, Serializer.serialize(this));

			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE Doors SET Door = ? WHERE Name = ?");

			statement.setString(1, Serializer.serialize(this));
			statement.setString(2, name);
			
			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(Location<World> location) {
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
	
	public static void init() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Doors");
		    
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				cache.put(result.getString("Name"), Serializer.deserializeDoor(result.getString("Door")));
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
