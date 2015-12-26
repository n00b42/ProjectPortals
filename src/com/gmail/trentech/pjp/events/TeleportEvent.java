package com.gmail.trentech.pjp.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TeleportEvent extends AbstractEvent implements Cancellable {
	
	private boolean cancelled = false;
	private Cause cause;
	private Location<World> src;
	private Location<World> dest;
	
	public TeleportEvent(Location<World> src, Location<World> dest, Cause cause){
		this.src = src;
		this.setDest(dest);
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
}
