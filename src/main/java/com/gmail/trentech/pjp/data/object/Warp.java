package com.gmail.trentech.pjp.data.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.gmail.trentech.pjp.utils.Serializer;

public class Warp extends PortalBase {

	private static ConcurrentHashMap<String, Warp> cache = new ConcurrentHashMap<>();
	
	public Warp(String destination, String rotation, double price) {
		super(destination, rotation, price);
	}

	public static Optional<Warp> get(String name) {
		if(cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}
		
		return Optional.empty();
	}

	public static ConcurrentHashMap<String, Warp> all(){
		return cache;
	}
	
	public void create(String name) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Warps (Name, Warp) VALUES (?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, Serializer.serialize(this));

			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(String name) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE Warps SET Warp = ? WHERE Name = ?");

			statement.setString(1, Serializer.serialize(this));
			statement.setString(2, name);
			
			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(String name) {
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
	
	public static void init() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Warps");
		    
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				cache.put(result.getString("Name"), Serializer.deserializeWarp(result.getString("Warp")));
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
