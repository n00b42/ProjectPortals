package com.gmail.trentech.pjp.data.portal;

import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.data.DataQueries;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Teleport;

public class PortalBase extends SQLUtils implements DataSerializable {

	protected String name;
	protected Optional<String> server = Optional.empty();
	protected Optional<World> world = Optional.empty();
	protected Optional<Vector3d> vector3d = Optional.empty();
	protected Rotation rotation = Rotation.EAST;
	protected double price = 0;

	protected PortalBase(Optional<String> server, Optional<World> world, Optional<Location<World>> location, Rotation rotation, double price) {
		this.server = server;
		this.world = world;
		if(location.isPresent()) {
			this.vector3d = Optional.of(location.get().getPosition());
			this.world = Optional.of(location.get().getExtent());
		}
		this.rotation = rotation;
		this.price = price;
	}
	
	protected PortalBase(String name, Optional<String> server, Optional<World> world, Optional<Location<World>> location, Rotation rotation, double price) {
		this.name = name;
		this.server = server;
		this.world = world;
		if(location.isPresent()) {
			this.vector3d = Optional.of(location.get().getPosition());
			this.world = Optional.of(location.get().getExtent());
		}
		this.rotation = rotation;
		this.price = price;
	}
	
	protected PortalBase(Location<World> location, Rotation rotation) {
		this.vector3d = Optional.of(location.getPosition());
		this.world = Optional.of(location.getExtent());
		this.rotation = rotation;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Optional<Location<World>> getLocation() {
		if(world.isPresent()) {
			if(vector3d.isPresent()) {
				Vector3d vector3d = this.vector3d.get();
				
				if(vector3d.getX() == 0 && vector3d.getY() == 0 && vector3d.getZ() == 0) {	
					return Optional.of(Teleport.getRandomLocation(world.get()));	
				} else {
					return Optional.of(new Location<World>(world.get(), vector3d));
				}
			} else {
				return Optional.of(world.get().getSpawnLocation());
			}
		} else {
			return Optional.empty();
		}
	}
	
	public void setLocation(Location<World> location) {
		this.vector3d = Optional.of(location.getPosition());
		this.world = Optional.of(location.getExtent());
		this.server = Optional.empty();
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	public Optional<String> getServer() {
		return server;
	}
	
	public void setServer(String server) {
		this.vector3d = Optional.empty();
		this.world = Optional.empty();
		this.server = Optional.of(server);
	}

	public Optional<World> getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.vector3d = Optional.empty();
		this.world = Optional.of(world);
		this.server = Optional.empty();
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer().set(DataQueries.ROTATION, rotation.getName()).set(DataQueries.PRICE, price);

		if(server.isPresent()) {
			container.set(DataQueries.SERVER, server.get());
		}
		
		if(world.isPresent()) {
			container.set(DataQueries.WORLD, world.get().getName());
		}
		
		if(vector3d.isPresent()) {
			Vector3d vector3d = this.vector3d.get();
			container.set(DataQueries.VECTOR3D, DataTranslators.VECTOR_3_D.translate(vector3d));
		}
		
		return container;
	}

}
