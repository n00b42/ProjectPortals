package com.gmail.trentech.pjp.portal;

import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.rotation.Rotation;

public class PortalProperties {

	private final String name;
	private final Optional<String> server;
	private final Optional<World> world;
	private final Optional<Location<World>> location;
	private final Rotation rotation;
	private final double price;	
	private final Particle particle;
	private final Optional<ParticleColor> color;

	public PortalProperties(String name, Optional<String> server, Optional<World> world, Optional<Location<World>> location, Rotation rotation, Particle particle, Optional<ParticleColor> color, double price) {
		this.name = name;
		this.server = server;
		this.world = world;
		this.location = location;
		this.rotation = rotation;
		this.price = price;
		this.particle = particle;
		this.color = color;
	}

	public Optional<String> getServer() {
		return server;
	}

	public Optional<World> getWorld() {
		return world;
	}

	public Optional<Location<World>> getLocation() {
		return location;
	}
	
	public Rotation getRotation() {
		return rotation;
	}

	public double getPrice() {
		return price;
	}

	public Particle getParticle() {
		return particle;
	}

	public Optional<ParticleColor> getColor() {
		return color;
	}

	public String getName() {
		return name;
	}
}
