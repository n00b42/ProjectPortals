package com.gmail.trentech.pjp.portals;

import java.util.Optional;

import com.gmail.trentech.pjp.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class Lever {
	
	public static Optional<String> get(String locationName){
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		if(config.getNode("Levers", locationName).getString() == null){
			return Optional.empty();
		}
		
		return Optional.of(config.getNode("Levers", locationName).getString()); 
	}
	
	public static void remove(String locationName){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Levers").removeChild(locationName);
		configManager.save();
	}
	
	public static void save(String locationName, String destination){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Levers", locationName).setValue(destination);

		configManager.save();
	}
}
