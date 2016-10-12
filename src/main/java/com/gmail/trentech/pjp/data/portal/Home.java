package com.gmail.trentech.pjp.data.portal;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.rotation.Rotation;

public class Home extends PortalBase {

	public Home(Location<World> location, Rotation rotation) {
		super(location, rotation);
	}
}
