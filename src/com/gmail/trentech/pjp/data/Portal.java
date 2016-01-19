package com.gmail.trentech.pjp.data;

import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.Utils;

public class Portal implements DataSerializable {

	private final String name;
	private final String destination;

	public Portal(String name, World world, boolean random) {
		this.name = name;
		if(random){
			this.destination = world.getName() + ":random";
		}else{
			this.destination = world.getName() + ":spawn";
		}
	}
	
	public Portal(String name, Location<World> destination) {
		this.name = name;
		this.destination = destination.getExtent().getName() + ":" + destination.getBlockX() + "." + destination.getBlockY() + "." + destination.getBlockZ();
	}

	public String getName() {
		return name;
	}

	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
			return Optional.empty();
		}
		World world = Main.getGame().getServer().getWorld(args[0]).get();
		
		if(args[1].equalsIgnoreCase("random")){
			return Optional.of(Utils.getRandomLocation(world));
		}else if(args[1].equalsIgnoreCase("spawn")){
			return Optional.of(world.getSpawnLocation());
		}else{
			String[] coords = args[1].split("\\.");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			return Optional.of(world.getLocation(x, y, z));	
		}
	}

	@Override
	public int getContentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return new MemoryDataContainer().set(DataQuery.of("name"), name).set(DataQuery.of("world"), destination);
	}
}
