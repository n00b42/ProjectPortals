package com.gmail.trentech.pjp.portals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CuboidBuilder {

	private Optional<Location<World>> destination = Optional.empty();
	private Optional<Location<World>> location = Optional.empty();
	
	private Optional<LocationType> locationType = Optional.empty();
	private final World world;
	
	private static HashMap<Player, CuboidBuilder> activeBuilders = new HashMap<>();
	private static List<Player> creators = new ArrayList<>();

	public CuboidBuilder(World world, Location<World> destination, LocationType locationType) {
		this.world = world;
		if(destination != null){
			this.destination = Optional.of(destination);
		}
		if(locationType != null){
			this.locationType = Optional.of(locationType);
		}
	}
	
	public CuboidBuilder() {
		world = null;
	}

	public Optional<Location<World>> getDestination() {
		return destination;
	}

	public Optional<Location<World>> getLocation() {
		return location;
	}

	public void setLocation(Location<World> location) {
		this.location = Optional.of(location);
	}

	public static HashMap<Player, CuboidBuilder> getActiveBuilders() {
		return activeBuilders;
	}

	public static List<Player> getCreators() {
		return creators;
	}

	public Optional<LocationType> getLocationType() {
		return locationType;
	}

	public World getWorld() {
		return world;
	}
}

