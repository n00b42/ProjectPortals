package com.gmail.trentech.pjp.portals;

import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

import ninja.leaping.configurate.ConfigurationNode;

public class Door {

	private final String name;
	private final String destination;

	public Door(String name, String destination) {
		this.name = name;
		this.destination = destination;
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

	public static Optional<Door> get(Location<World> location){
		String name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		if(config.getNode("Doors", name).getString() == null){
			return Optional.empty();
		}
		
		String destination = config.getNode("Doors", name).getString();
		
		return Optional.of(new Door(name, destination)); 
	}
	
	public static void remove(Location<World> location){
		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Doors").removeChild(locationName);
		configManager.save();
	}
	
	public static void save(Location<World> location, String destination){
		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Doors", locationName).setValue(destination);

		configManager.save();
	}
}
