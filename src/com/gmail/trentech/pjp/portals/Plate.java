package com.gmail.trentech.pjp.portals;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Plate {

	private final Location<World> location;
	private final LocationType locationType;
	private final World world;
	
	public Plate(World world, Location<World> location, LocationType locationType){
		this.location = location;
		this.locationType = locationType;
		this.world = world;
	}

	public Location<World> getLocation() {
		return location;
	}

	public LocationType getLocationType() {
		return locationType;
	}
	
	public World getWorld() {
		return world;
	}
}
