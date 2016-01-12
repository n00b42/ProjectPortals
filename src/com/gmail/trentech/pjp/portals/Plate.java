package com.gmail.trentech.pjp.portals;

import java.util.Optional;

import com.gmail.trentech.pjp.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class Plate {
	
	public static Optional<String> get(String locationName){
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		if(config.getNode("Plates", locationName).getString() == null){
			return Optional.empty();
		}
		
		return Optional.of(config.getNode("Plates", locationName).getString()); 
	}
	
	public static void remove(String locationName){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Plates").removeChild(locationName);
		configManager.save();
	}
	
	public static void save(String locationName, String destination){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Plates", locationName).setValue(destination);

		configManager.save();
	}
}
