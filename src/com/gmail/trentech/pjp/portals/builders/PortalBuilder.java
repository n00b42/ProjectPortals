package com.gmail.trentech.pjp.portals.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.ConstructPortalEvent;
import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

public class PortalBuilder {

	private final String destination;
	private String name = UUID.randomUUID().toString();
	private boolean fill = false;
	private Optional<List<Location<World>>> regionFrame = Optional.empty();
	private Optional<List<Location<World>>> regionFill = Optional.empty();
	
	public PortalBuilder(String destination) {
		this.destination = destination;
	}

	public PortalBuilder(){
		destination = null;
	}

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
		List<Location<World>> frame = new ArrayList<>(regionFrame.get());
		List<Location<World>> fill = new ArrayList<>(regionFill.get());

		if(!Main.getGame().getEventManager().post(new ConstructPortalEvent(frame, fill, Cause.of(this)))) {
			boolean particles = new ConfigManager().getConfig().getNode("options", "particles").getBoolean();
			
			BlockState block = BlockTypes.AIR.getDefaultState();

			for(Location<World> location : fill){
				if(particles){
					Optional<Entity> optionalEntity = location.getExtent().createEntity(EntityTypes.LIGHTNING, location.getPosition());
					
					if (optionalEntity.isPresent()) {
						Lightning lightning = (Lightning) optionalEntity.get();
						location.getExtent().spawnEntity(lightning, Cause.of(this));
					}
					
					Utils.spawnParticles(location, 1.0, false);
				}
				
				location.setBlock(block);
			}
			
			if(particles){
				Main.createTask(name, fill);
			}
			
			Portal.save(new Portal(name, destination, frame, fill, null));
			
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
