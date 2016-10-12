package com.gmail.trentech.pjp.data.portal;

import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.rotation.Rotation;


public class Sign extends PortalBase {

	public Sign(Optional<String> server, Optional<World> world, Optional<Location<World>> location, Rotation rotation, double price) {
		super(server, world, location, rotation, price);
	}
}
