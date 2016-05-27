package com.gmail.trentech.pjp.effects;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleType.Colorable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;

public class Particle {

	private final String name;
	private final ParticleType type;
	private final long time;
	private static ThreadLocalRandom random = ThreadLocalRandom.current();
	
	protected Particle(String name, ParticleType type, long time) {
		this.name = name;
		this.type = type;
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	
	public ParticleType getType() {
		return type;
	}
	
	private long getTime() {
		return time;
	}
	
	public boolean isColorable() {
		if(getType() instanceof Colorable) {
			return true;
		}
		return false;
	}
	
	public void spawnParticle(Location<World> location, ParticleColor color, boolean player) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			if(isColorable()) {
				spawnColoredNonRepeat(location, color, player);
			}
		}
	}
	
	public void spawnParticle(Location<World> location, boolean player) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			spawnNonRepeat(location, player);
		}
	}

	public void createTask(String name, List<Location<World>> locations, ParticleColor color) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			if(isColorable()) {
				spawnColoredRepeat(name, locations, color);
			}
		}
	}
	
	public void createTask(String name, List<Location<World>> locations) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			spawnRepeat(name, locations);
		}
	}

	private void spawnNonRepeat(Location<World> location, boolean player) {
		ParticleEffect particle = ParticleEffect.builder().type(getType()).build();
		
		for(int i = 0; i < 9; i++) {
			if(player) {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble() - .5,random.nextDouble() - .5,random.nextDouble() - .5));
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble() - .5,random.nextDouble() - .5,random.nextDouble() - .5));
			}else {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
			}
		}
	}
	
	private void spawnColoredNonRepeat(Location<World>location, ParticleColor color, boolean player) {
		ColoredParticle particle = ColoredParticle.builder().color(color.getColor()).type((Colorable) getType()).build();
		
		for(int i = 0; i < 9; i++) {
			if(player) {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble() - .5,random.nextDouble() - .5,random.nextDouble() - .5));
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble() - .5,random.nextDouble() - .5,random.nextDouble() - .5));
			}else {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
			}
		}
	}
	
	private void spawnRepeat(String name, List<Location<World>> locations) {
		ParticleEffect particle = ParticleEffect.builder().type(getType()).build();
		
        Main.getGame().getScheduler().createTaskBuilder().interval(getTime(), TimeUnit.MILLISECONDS).name(name).execute(t -> {
        	
    		for(Location<World> location : locations) {
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    		}
        }).submit(Main.getPlugin());
	}
	
	private void spawnColoredRepeat(String name, List<Location<World>> locations, ParticleColor color) {
		ColoredParticle particle = ColoredParticle.builder().color(color.getColor()).type((Colorable) getType()).build();
		
        Main.getGame().getScheduler().createTaskBuilder().interval(getTime(), TimeUnit.MILLISECONDS).name(name).execute(t -> {
        	
    		for(Location<World> location : locations) {
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    		}
        }).submit(Main.getPlugin());
	}
}
