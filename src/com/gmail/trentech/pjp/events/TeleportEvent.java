package com.gmail.trentech.pjp.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.portals.LocationType;

public class TeleportEvent extends AbstractEvent implements Cancellable {
	
	private boolean cancelled = false;
	private final Cause cause;
	private final Location<World> src;
	private Location<World> dest;
	private final LocationType locationType;
	
	public TeleportEvent(Location<World> src, Location<World> dest, LocationType locationType, Cause cause){
		this.src = src;
		this.setDest(dest);
		this.locationType = locationType;
		this.cause = cause;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;		
	}
	
	@Override
	public Cause getCause() {
		return cause;
	}

	public Location<World> getDest() {
		return dest;
	}

	public void setDest(Location<World> dest) {
		this.dest = dest;
	}

	public Location<World> getSrc() {
		return src;
	}

	public LocationType getLocationType() {
		return locationType;
	}
}
