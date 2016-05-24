package com.gmail.trentech.pjp.data.object;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.ConstructPortalEvent;
import com.gmail.trentech.pjp.utils.Rotation;

public class PortalBuilder {

	private final String name;
	private final String destination;
	private final String rotation;
	private final double price;
	private final boolean bungee;
	private final String particle;
	private boolean fill = false;
	private List<Location<World>> regionFrame;
	private List<Location<World>> regionFill;
	
	public PortalBuilder(String name, String destination, Rotation rotation, String particle, double price, boolean bungee) {
		this.name = name;
		this.destination = destination;
		this.rotation = rotation.getName();
		this.price = price;
		this.bungee = bungee;
		this.particle = particle;
		this.regionFrame = new ArrayList<>();
		this.regionFill = new ArrayList<>();
	}

	public List<Location<World>> getRegionFill() {
		return regionFill;
	}
	
	public List<Location<World>> getRegionFrame() {
		return regionFrame;
	}

	public String getName() {
		return name;
	}

	public PortalBuilder addFrame(Location<World> location) {
		regionFrame.add(location);
		return this;
	}
	
	public PortalBuilder removeFrame(Location<World> location) {
		regionFrame.remove(location);
		return this;
	}
	
	public PortalBuilder addFill(Location<World> location) {
		regionFill.add(location);
		return this;
	}
	
	public PortalBuilder removeFill(Location<World> location) {
		regionFill.remove(location);
		return this;
	}
	
	public boolean isFill() {
		return fill;
	}

	public PortalBuilder setFill(boolean fill) {
		this.fill = fill;
		return this;
	}
	
	public boolean build() {
		if(regionFrame.isEmpty() || regionFill.isEmpty()) {
			return false;
		}

		if(!Main.getGame().getEventManager().post(new ConstructPortalEvent(regionFrame, regionFill, Cause.of(NamedCause.source(this))))) {
			BlockState block = BlockTypes.AIR.getDefaultState();

			List<String> frame = new ArrayList<>();

			for(Location<World> location : regionFrame) {
				frame.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
			}
			
			List<String> fill = new ArrayList<>();
			
			for(Location<World> location : regionFill) {
				fill.add(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ());
				location.getExtent().setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), block, false, Cause.of(NamedCause.source(Main.getPlugin())));
			}

			new Portal(getName(), destination, rotation, frame, fill, particle, price, bungee).create();

			return true;
		}
		return false;
	}


}
