package com.gmail.trentech.pjp.portals.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.events.ConstructPortalEvent;
import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.utils.ConfigManager;

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

		if(!Main.getGame().getEventManager().post(new ConstructPortalEvent(frame, fill, Cause.of(NamedCause.source(this))))) {
			BlockState block = BlockTypes.AIR.getDefaultState();

			for(Location<World> location : fill){
				String[] split = new ConfigManager().getConfig().getNode("options", "particles", "type", "creation").getString().split(":");
				
				Optional<Particle> optionalParticle = Particles.get(split[0]);
				
				if(optionalParticle.isPresent()){
					Particle particle = optionalParticle.get();
					
					if(split.length == 2 && particle.isColorable()){
						Optional<ParticleColor> optionalColors = ParticleColor.get(split[1]);
						
						if(optionalColors.isPresent()){
							particle.spawnParticle(location, optionalColors.get());
						}else{
							particle.spawnParticle(location);
						}
					}else{
						particle.spawnParticle(location);
					}
				}
				
				location.getExtent().setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), block, false, Cause.of(NamedCause.source(Main.getPlugin())));
			}

			Portal portal = new Portal(name, destination, frame, fill, null, 0, null);
			Portal.save(portal);

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
