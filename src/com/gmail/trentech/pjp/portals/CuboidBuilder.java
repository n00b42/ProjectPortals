package com.gmail.trentech.pjp.portals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CuboidBuilder {

	private Location<World> destination;
	private Location<World> location;
	
	private static HashMap<Player, CuboidBuilder> activeBuilders = new HashMap<>();
	private static List<Player> creators = new ArrayList<>();

	public CuboidBuilder(Location<World> destination) {
		this.destination = destination;
	}
	
	public CuboidBuilder() {
		
	}

	public Location<World> getDestination() {
		return destination;
	}

	public Location<World> getLocation() {
		return location;
	}

	public void setLocation(Location<World> location) {
		this.location = location;
	}

	public static HashMap<Player, CuboidBuilder> getActiveBuilders() {
		return activeBuilders;
	}

	public static List<Player> getCreators() {
		return creators;
	}
}

