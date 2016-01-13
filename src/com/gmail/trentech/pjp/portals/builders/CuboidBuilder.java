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
import com.gmail.trentech.pjp.events.ConstructCuboidEvent;
import com.gmail.trentech.pjp.portals.Cuboid;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Region;
import com.gmail.trentech.pjp.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class CuboidBuilder extends Builder{

	private Optional<Location<World>> startLocation = Optional.empty();
	private String name = UUID.randomUUID().toString();
	private Location<World> endLocation;
	private BlockState block = Main.getGame().getRegistry().createBuilder(BlockState.Builder.class).blockType(BlockTypes.OBSIDIAN).build();
	
	public CuboidBuilder(String destination) {
		super(destination);
	}

	public CuboidBuilder(){}

	public Optional<Location<World>> getStartLocation(){
		return startLocation;
	}
	
	public String getName(){
		return name;
	}
	
	public CuboidBuilder name(String name){
		this.name = name;
		return this;
	}
	
	public CuboidBuilder start(Location<World> startLocation) {
		this.startLocation = Optional.of(startLocation);
		return this;
	}

	public CuboidBuilder end(Location<World> endLocation) {
		this.endLocation = endLocation;
		return this;
	}
	
	public boolean build(){
		if(!startLocation.isPresent()){
			return false;
		}
		
		Region region = new Region(startLocation.get(), endLocation);
		
		List<String> regionList = new ArrayList<>();

		for(Location<World> location : region){
			regionList.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}

		if(!Main.getGame().getEventManager().post(new ConstructCuboidEvent(region, Cause.of(this)))) {			
			ConfigurationNode config = new ConfigManager().getConfig();
			for(Location<World> location : region){
	        	if(!config.getNode("Options", "Cube", "Replace-Frame").getBoolean()){	
	        		continue;
	        	}
	        	
            	if(!location.getBlockType().equals(BlockTypes.AIR)){
            		location.setBlock(block);
            	}else if(config.getNode("Options", "Cube", "Fill").getBoolean()){
//            		BlockState block = BlockState.builder().blockType(BlockTypes.FLOWING_WATER).build();
//            		location.setBlock(block);
            	}
            	
	    		if(config.getNode("Options", "Show-Particles").getBoolean()){
	    			Resource.spawnParticles(location, 1.0, false);
	    		}
			}
			Cuboid.save(new Cuboid(name, destination, regionList));
			return true;
		}
		return false;
	}
	
	public boolean build(BlockState block){
		this.block = block;
		return build();
	}
	
}
