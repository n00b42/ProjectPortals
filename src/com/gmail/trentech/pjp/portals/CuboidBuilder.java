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
	
	private boolean spawn = false;
	
	private static HashMap<Player, CuboidBuilder> activeBuilders = new HashMap<>();
	private static List<Player> creators = new ArrayList<>();

	public CuboidBuilder(Location<World> destination, boolean spawn) {
		this.destination = Optional.of(destination);
		this.spawn = spawn;
	}
	
	public CuboidBuilder() {
		
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

	public boolean isSpawn() {
		return spawn;
	}
}

