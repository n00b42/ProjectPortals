package com.gmail.trentech.pjp.events;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class CuboidConstructEvent extends AbstractEvent implements Cancellable {

	private boolean cancelled = false;

	private Cause cause;
	private Optional<List<String>> locations = Optional.empty();
	
	public CuboidConstructEvent(List<String> locations, Cause cause){
		this.cause = cause;
		this.locations = Optional.of(locations);
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

	public Optional<List<String>> getLocations() {
		return locations;
	}
}
