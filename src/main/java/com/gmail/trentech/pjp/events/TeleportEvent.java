package com.gmail.trentech.pjp.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TeleportEvent extends AbstractEvent implements Cancellable {
	
	private boolean cancelled = false;
	private final Player target;
	private final Cause cause;
	private final Location<World> source;
	private Location<World> destination;
	private double price;
	
	public TeleportEvent(Player target, Location<World> source, Location<World> destination, double price, Cause cause) {
		this.target = target;
		this.source = source;
		this.setDestination(destination);
		this.price = price;
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

	public Location<World> getDestination() {
		return destination;
	}

	public void setDestination(Location<World> destination) {
		this.destination = destination;
	}

	public double getPrice() {
		return price;
	}
	
	public Location<World> getSource() {
		return source;
	}

	public Player getTarget() {
		return target;
	}
}
