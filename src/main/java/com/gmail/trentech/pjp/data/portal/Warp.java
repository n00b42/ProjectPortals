package com.gmail.trentech.pjp.data.portal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.Serializer;

public class Warp extends PortalBase {

	private static ConcurrentHashMap<String, Warp> cache = new ConcurrentHashMap<>();

	public Warp(Optional<String> server, Optional<World> world, Optional<Location<World>> location, Rotation rotation, double price) {
		super(server, world, location, rotation, price);
	}

	public static Optional<Warp> get(String name) {
		if (cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}

		return Optional.empty();
	}

	public static ConcurrentHashMap<String, Warp> all() {
		return cache;
	}

	public void create(String name) {
		this.name = name;
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

	public void update() {
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

	public void remove() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from Warps WHERE Name = ?");

			statement.setString(1, name);
			statement.executeUpdate();

			connection.close();

			cache.remove(name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM Warps");

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String name = result.getString("Name");

				Warp warp = Serializer.deserializeWarp(result.getString("Warp"));
				warp.setName(name);
				cache.put(name, warp);
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
