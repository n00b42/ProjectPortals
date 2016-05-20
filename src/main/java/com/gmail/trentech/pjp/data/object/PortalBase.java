package com.gmail.trentech.pjp.data.object;

import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.DataQueries;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Utils;

public class PortalBase extends SQLUtils implements DataSerializable {

	protected String destination;
	protected String rotation;
	protected double price;

	protected PortalBase(String destination, String rotation, double price) {
		this.destination = destination;
		this.rotation = rotation;
		this.price = price;
	}

	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public Rotation getRotation() {
		return Rotation.get(rotation).get();
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation.getName();
	}
	
	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		Optional<World> optionalWorld = Main.getGame().getServer().getWorld(args[0]);
		
		if(!optionalWorld.isPresent()) {
			return Optional.empty();
		}
		World world = optionalWorld.get();
		
		if(args[1].equalsIgnoreCase("random")) {
			return Optional.of(Utils.getRandomLocation(world));
		}else if(args[1].equalsIgnoreCase("spawn")) {
			return Optional.of(world.getSpawnLocation());
		}else{
			String[] coords = args[1].split("\\.");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			return Optional.of(world.getLocation(x, y, z));	
		}
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(DataQueries.DESTINATION, destination).set(DataQueries.ROTATION, rotation).set(DataQueries.PRICE, price);
    }

}
