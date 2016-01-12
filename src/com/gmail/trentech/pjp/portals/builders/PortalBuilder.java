package com.gmail.trentech.pjp.portals.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.ConstructPortalEvent;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class PortalBuilder extends Builder{

	private String name = UUID.randomUUID().toString();
	private Optional<List<Location<World>>> region = Optional.empty();

	public PortalBuilder(String destination) {
		super(destination);
	}

	public PortalBuilder(){}

	public Optional<List<Location<World>>> getRegion() {
		return region;
	}

	public String getName(){
		return name;
	}
	
	public PortalBuilder name(String name){
		this.name = name;
		return this;
	}
	
	public PortalBuilder add(Location<World> location) {
		if(!region.isPresent()){
			region = Optional.of(new ArrayList<Location<World>>());
		}
		region.get().add(location);
		return this;
	}
	
	public PortalBuilder remove(Location<World> location) {
		if(!region.isPresent()){
			region.get().remove(location);
		}
		return this;
	}
	
	public boolean build(){
		if(!region.isPresent()){
			return false;
		}

		List<String> regionList = new ArrayList<>();
		
		for(Location<World> location : region.get()){
			regionList.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}
		
		if(!Main.getGame().getEventManager().post(new ConstructPortalEvent(regionList, Cause.of(this)))) {
			ConfigManager configManager = new ConfigManager("portals.conf");
			ConfigurationNode config = configManager.getConfig();

			config.getNode("Portals", name, "Destination").setValue(destination);
			config.getNode("Portals", name, "Region").setValue(regionList);

			configManager.save();
			
			for(Location<World> location : region.get()){
	    		BlockState block = BlockState.builder().blockType(BlockTypes.AIR).build();
	    		location.setBlock(block);
		    	
				if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
					Resource.spawnParticles(location, 1.0, false);
				}
			}
			return true;
		}
		return false;
	}
}
