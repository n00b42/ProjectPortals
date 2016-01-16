package com.gmail.trentech.pjp.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(String folder, String configName) {
		folder = "config" + File.separator + "projectportals" + File.separator + folder;
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, configName);
		
		create();
		load();
		init();
	}
	
	public ConfigManager(String configName) {
		String folder = "config" + File.separator + "projectportals";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, configName);
		
		create();
		load();
		init();
	}
	
	public ConfigManager() {
		String folder = "config" + File.separator + "projectportals";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, "config.conf");
		
		create();
		load();
		init();
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save(){
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
	
	private void init() {
		if(file.getName().equalsIgnoreCase("config.conf")){
//			if(config.getNode("Options", "Cube", "Size").getString() == null) {
//				config.getNode("Options", "Cube", "Size").setValue(100).setComment("Maximum portal region size");
//			}
			if(config.getNode("Options", "Cube", "Replace-Frame").getString() == null) {
				config.getNode("Options", "Cube", "Replace-Frame").setValue(true);
			}
//			if(config.getNode("Options", "Cube", "Fill").getString() == null) {
//				config.getNode("Options", "Cube", "Fill").setValue(false).setComment("Fill space in cube with water");
//			}
			if(config.getNode("Options", "Portal-Size").getString() == null) {
				config.getNode("Options", "Portal-Size").setValue(100).setComment("Maximum number of blocks a portal can use");
			}
			if(config.getNode("Options", "Homes").getString() == null) {
				config.getNode("Options", "Homes").setValue(5).setComment("Default number of homes a player can have");
			}
			if(config.getNode("Options", "Show-Particles").getString() == null) {
				config.getNode("Options", "Show-Particles").setValue(true).setComment("Display particle effects on portal creation and teleporting");
			}
			if(config.getNode("Options", "Random-Spawn-Radius").getString() == null) {
				config.getNode("Options", "Random-Spawn-Radius").setValue(5000).setComment("World radius for random spawn portals.");
			}
			if(config.getNode("Options", "Command-Alias", "cube").getString() == null) {
				config.getNode("Options", "Command-Alias", "cube").setValue("cb");
			}
			if(config.getNode("Options", "Command-Alias", "portal").getString() == null) {
				config.getNode("Options", "Command-Alias", "portal").setValue("p");
			}
			if(config.getNode("Options", "Command-Alias", "button").getString() == null) {
				config.getNode("Options", "Command-Alias", "button").setValue("btn");
			}
			if(config.getNode("Options", "Command-Alias", "door").getString() == null) {
				config.getNode("Options", "Command-Alias", "door").setValue("d");
			}
			if(config.getNode("Options", "Command-Alias", "plate").getString() == null) {
				config.getNode("Options", "Command-Alias", "plate").setValue("pl");
			}
			if(config.getNode("Options", "Command-Alias", "lever").getString() == null) {
				config.getNode("Options", "Command-Alias", "lever").setValue("l");
			}
			if(config.getNode("Options", "Command-Alias", "home").getString() == null) {
				config.getNode("Options", "Command-Alias", "home").setValue("h");
			}
			if(config.getNode("Options", "Command-Alias", "warp").getString() == null) {
				config.getNode("Options", "Command-Alias", "warp").setValue("wp");
			}
			if(config.getNode("Options", "Command-Alias", "back").getString() == null) {
				config.getNode("Options", "Command-Alias", "back").setValue("bk");
			}
			if(config.getNode("Options", "Modules", "Cubes").getString() == null) {
				config.getNode("Options", "Modules", "Cubes").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Portals").getString() == null) {
				config.getNode("Options", "Modules", "Portals").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Buttons").getString() == null) {
				config.getNode("Options", "Modules", "Buttons").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Doors").getString() == null) {
				config.getNode("Options", "Modules", "Doors").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Plates").getString() == null) {
				config.getNode("Options", "Modules", "Plates").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Levers").getString() == null) {
				config.getNode("Options", "Modules", "Levers").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Signs").getString() == null) {
				config.getNode("Options", "Modules", "Signs").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Warps").getString() == null) {
				config.getNode("Options", "Modules", "Warps").setValue(true);
			}
			if(config.getNode("Options", "Modules", "Homes").getString() == null) {
				config.getNode("Options", "Modules", "Homes").setValue(true);
			}
			save();
		}else if(file.getName().equalsIgnoreCase("portals.conf")){
			if(config.getNode("Buttons").getString() == null) {
				config.getNode("Buttons").setComment("DO NOT EDIT THIS FILE");
				save();
			}			
		}else if(file.getName().equalsIgnoreCase("warps.conf")){
			if(config.getNode("Warps").getString() == null) {
				config.getNode("Warps").setComment("DO NOT EDIT THIS FILE");
				save();
			}
		}else if(file.getPath().contains("Players")){
			if(config.getNode("Homes").getString() == null) {
				config.getNode("Homes").setComment("DO NOT EDIT THIS FILE");
				save();
			}
		}
		
	}

	private void create(){
		if(!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();		
			} catch (IOException e) {				
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}
	
	private void load(){
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

	public boolean removeCuboidLocation(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Cuboids").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			
			List<String> list = config.getNode("Cuboids", uuid, "Locations").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

	    	if(!list.contains(locationName)){
	    		continue;
	    	}

			for(String loc : list){
	        	String[] info = loc.split("\\.");

	        	Location<World> location = Main.getGame().getServer().getWorld(info[0]).get().getLocation(Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]));

            	if(location.getBlockType().equals(BlockTypes.FLOWING_WATER)){
            		BlockState block = BlockState.builder().blockType(BlockTypes.AIR).build();
            		location.setBlock(block);
            	}
			}
			
			config.getNode("Cuboids").removeChild(uuid);
			save();
			
			return true;
		}
		return false;
	}

	public ConfigurationNode getCuboid(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Cuboids").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();

	    	List<String> list = config.getNode("Cuboids", uuid, "Locations").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

	    	if(!list.contains(locationName)){
	    		continue;
	    	}
	    	return config.getNode("Cuboids", uuid);
		}
		return null;
	}

}
