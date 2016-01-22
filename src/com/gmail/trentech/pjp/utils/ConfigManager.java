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
			if(config.getNode("options", "portal_size").getString() == null) {
				config.getNode("options", "portal_size").setValue(100).setComment("Maximum number of blocks a portal can use");
			}
			if(config.getNode("options", "homes").getString() == null) {
				config.getNode("options", "homes").setValue(5).setComment("Default number of homes a player can have");
			}
			if(config.getNode("options", "particles").getString() == null) {
				config.getNode("options", "particles").setValue(true).setComment("Display particle effects on portal creation and teleporting");
			}
			if(config.getNode("options", "random_spawn_radius").getString() == null) {
				config.getNode("options", "random_spawn_radius").setValue(5000).setComment("World radius for random spawn portals.");
			}
			if(config.getNode("settings", "commands").getString() == null){
				config.getNode("settings", "commands").setComment("Allow to set custom command aliases");
			}
			if(config.getNode("settings", "commands", "portal").getString() == null) {
				config.getNode("settings", "commands", "portal").setValue("p");
			}
			if(config.getNode("settings", "commands", "button").getString() == null) {
				config.getNode("settings", "commands", "button").setValue("btn");
			}
			if(config.getNode("settings", "commands", "door").getString() == null) {
				config.getNode("settings", "commands", "door").setValue("d");
			}
			if(config.getNode("settings", "commands", "plate").getString() == null) {
				config.getNode("settings", "commands", "plate").setValue("pl");
			}
			if(config.getNode("settings", "commands", "sign").getString() == null) {
				config.getNode("settings", "commands", "sign").setValue("s");
			}
			if(config.getNode("settings", "commands", "lever").getString() == null) {
				config.getNode("settings", "commands", "lever").setValue("l");
			}
			if(config.getNode("settings", "commands", "home").getString() == null) {
				config.getNode("settings", "commands", "home").setValue("h");
			}
			if(config.getNode("settings", "commands", "warp").getString() == null) {
				config.getNode("settings", "commands", "warp").setValue("wp");
			}
			if(config.getNode("settings", "commands", "back").getString() == null) {
				config.getNode("settings", "commands", "back").setValue("bk");
			}
			if(config.getNode("settings", "modules").getString() == null) {
				config.getNode("settings", "modules").setComment("Toggle on and off specific features");
			}
			if(config.getNode("settings", "modules", "portals").getString() == null) {
				config.getNode("settings", "modules", "portals").setValue(true);
			}
			if(config.getNode("settings", "modules", "buttons").getString() == null) {
				config.getNode("settings", "modules", "buttons").setValue(true);
			}
			if(config.getNode("settings", "modules", "doors").getString() == null) {
				config.getNode("settings", "modules", "doors").setValue(true);
			}
			if(config.getNode("settings", "modules", "plates").getString() == null) {
				config.getNode("settings", "modules", "plates").setValue(true);
			}
			if(config.getNode("settings", "modules", "levers").getString() == null) {
				config.getNode("settings", "modules", "levers").setValue(true);
			}
			if(config.getNode("settings", "modules", "signs").getString() == null) {
				config.getNode("settings", "modules", "signs").setValue(true);
			}
			if(config.getNode("settings", "modules", "warps").getString() == null) {
				config.getNode("settings", "modules", "warps").setValue(true);
			}
			if(config.getNode("settings", "modules", "homes").getString() == null) {
				config.getNode("settings", "modules", "homes").setValue(true);
			}
			save();
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
