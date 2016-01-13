package com.gmail.trentech.pjp.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import com.gmail.trentech.pjp.utils.Region;

public class ConstructCuboidEvent extends AbstractEvent implements Cancellable {

	private boolean cancelled = false;

	private final Cause cause;
	private final Region region;
	
	public ConstructCuboidEvent(Region region, Cause cause){
		this.cause = cause;
		this.region = region;
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

	public Region getRegion() {
		return region;
	}
}
