package com.gmail.trentech.pjp.portals;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Plate {

	private final Location<World> location;
	private final boolean spawn;
	
	public Plate(Location<World> location, boolean spawn){
		this.location = location;
		this.spawn = spawn;
	}

	public Location<World> getLocation() {
		return location;
	}

	public boolean isSpawn() {
		return spawn;
	}
}
