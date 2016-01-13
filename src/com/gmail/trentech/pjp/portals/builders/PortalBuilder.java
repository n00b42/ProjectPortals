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
import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

public class PortalBuilder extends Builder{

	private String name = UUID.randomUUID().toString();
	private boolean fill = false;
	private Optional<List<Location<World>>> regionFrame = Optional.empty();
	private Optional<List<Location<World>>> regionFill = Optional.empty();
	
	public PortalBuilder(String destination) {
		super(destination);
	}

	public PortalBuilder(){}

	public Optional<List<Location<World>>> getRegionFill() {
		return regionFill;
	}
	
	public Optional<List<Location<World>>> getRegionFrame() {
		return regionFrame;
	}

	public String getName(){
		return name;
	}
	
	public PortalBuilder name(String name){
		this.name = name;
		return this;
	}
	
	public PortalBuilder addFill(Location<World> location) {
		if(!regionFill.isPresent()){
			regionFill = Optional.of(new ArrayList<Location<World>>());
		}
		regionFill.get().add(location);
		return this;
	}
	public PortalBuilder addFrame(Location<World> location) {
		if(!regionFrame.isPresent()){
			regionFrame = Optional.of(new ArrayList<Location<World>>());
		}
		regionFrame.get().add(location);
		return this;
	}
	
	public PortalBuilder removeFrame(Location<World> location) {
		if(regionFrame.isPresent()){
			regionFrame.get().remove(location);
		}
		return this;
	}
	
	public PortalBuilder removeFill(Location<World> location) {
		if(regionFill.isPresent()){
			regionFill.get().remove(location);
		}
		return this;
	}
	
	public boolean build(){
		if(!regionFrame.isPresent() || !regionFill.isPresent()){
			return false;
		}
		regionFrame.get().addAll(regionFill.get());
		List<String> regionList = new ArrayList<>();
		
		for(Location<World> location : regionFrame.get()){
			regionList.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
		}

		if(!Main.getGame().getEventManager().post(new ConstructPortalEvent(regionFrame.get(), Cause.of(this)))) {
			BlockState block = BlockState.builder().blockType(BlockTypes.AIR).build();
			
			for(Location<World> location : regionFill.get()){
	    		location.setBlock(block);

				if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
					Resource.spawnParticles(location, 1.0, false);
				}
			}
			
			Portal.save(new Portal(name, destination, regionList));
			
			return true;
		}
		return false;
	}

	public boolean isFill() {
		return fill;
	}

	public PortalBuilder fill(boolean fill) {
		this.fill = fill;
		return this;
	}
}
