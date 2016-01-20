package com.gmail.trentech.pjp.portals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

import ninja.leaping.configurate.ConfigurationNode;

public class Portal {

	private final String name;
	public final String destination;
	private final List<String> frame;
	private final List<String> fill;

	public Portal(String uuid, String destination, List<String> frame, List<String> fill) {
		this.name = uuid;
		this.destination = destination;
		this.frame = frame;
		this.fill = fill;
	}
	
	public Portal(String uuid, String destination, List<Location<World>> frame, List<Location<World>> fill, String dummy) {
		this.name = uuid;
		this.destination = destination;
		
		this.frame = new ArrayList<>();
		this.fill = new ArrayList<>();
		
		for(Location<World> location : frame){
			this.frame.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
		for(Location<World> location : fill){
			this.fill.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
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

	public List<Location<World>> getFrame() {
		List<Location<World>> list = new ArrayList<>();
		
		for(String loc : frame){
			String[] args = loc.split(":");
			
			if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(args[0]).get();

			String[] coords = args[1].split("\\.");

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			list.add(world.getLocation(x, y, z));	
		}
		return list;
	}
	
	public List<Location<World>> getFill() {
		List<Location<World>> list = new ArrayList<>();
		
		for(String loc : fill){
			String[] args = loc.split(":");
			
			if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(args[0]).get();

			String[] coords = args[1].split("\\.");

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			list.add(world.getLocation(x, y, z));	
		}
		return list;
	}

	public static Optional<Portal> get(Location<World> location){
		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String name = node.getKey().toString();

	    	List<String> frame = config.getNode("Portals", name, "Frame").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
	    	List<String> fill = config.getNode("Portals", name, "Fill").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
	    	
	    	if(!frame.contains(locationName) && !fill.contains(locationName)){
	    		continue;
	    	}
	    	
	    	String destination = config.getNode("Portals", name, "Destination").getString();

	    	return Optional.of(new Portal(name, destination, frame, fill));
		}
		return Optional.empty();
	}
	
	public static Optional<Portal> getByName(String name){
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		if(config.getNode("Portals", name).getString() != null){
			String destination = config.getNode("Portals", name, "Destination").getString();
			
			List<String> frame = config.getNode("Portals", name, "Frame").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			List<String> fill = config.getNode("Portals", name, "Fill").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

		    return Optional.of(new Portal(name, destination, frame, fill));
		}
		return Optional.empty();
	}
	
	public static void remove(String name){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();
		
		config.getNode("Portals").removeChild(name);
		configManager.save();
		
		for(Task task : Main.getGame().getScheduler().getScheduledTasks()){
			if(task.getName().contains(name)){
				task.cancel();
			}
		}
	}
	
	public static void save(Portal portal){	
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Portals", portal.getName(), "Destination").setValue(portal.destination);
		config.getNode("Portals", portal.getName(), "Frame").setValue(portal.frame);
		config.getNode("Portals", portal.getName(), "Fill").setValue(portal.fill);
		
		configManager.save();
	}
	
	public static List<Portal> list(){
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		List<Portal> list = new ArrayList<>();
		
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String name = node.getKey().toString();

			String destination = config.getNode("Portals", name, "Destination").getString();
			
			List<String> frame = config.getNode("Portals", name, "Frame").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			List<String> fill = config.getNode("Portals", name, "Fill").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			
			list.add(new Portal(name, destination, frame, fill));
		}

		return list;
	}

}
