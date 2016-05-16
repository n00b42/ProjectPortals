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

	protected Particle(String name, ParticleType type, long time, boolean colored) {
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
	
	public void spawnParticle(Location<World> location, ParticleColor color) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			if(isColorable()) {
				spawnColoredNonRepeat(location, color);
			}
		}
	}
	
	public void spawnParticle(Location<World> location) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			spawnNonRepeat(location);
		}
	}
	
	private void spawnNonRepeat(Location<World> location) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for(int i = 0; i < 9; i++) {	
			ParticleEffect particle = ParticleEffect.builder().type(getType()).build();
			
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
		}
	}
	
	private void spawnColoredNonRepeat(Location<World>location, ParticleColor color) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for(int i = 0; i < 9; i++) {	
			ColoredParticle particle = ColoredParticle.builder().color(color.getColor()).type((Colorable) getType()).build();
			
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
		}
	}
	
	public void createTask(String name, List<Location<World>> locations, ParticleColor color) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			ThreadLocalRandom random = ThreadLocalRandom.current();

			if(isColorable()) {
				spawnColoredRepeat(name, locations, random, color);
			}
		}
	}
	
	public void createTask(String name, List<Location<World>> locations) {
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()) {
			ThreadLocalRandom random = ThreadLocalRandom.current();

			spawnRepeat(name, locations, random);
		}
	}

	private void spawnRepeat(String name, List<Location<World>> locations, ThreadLocalRandom random) {
        Main.getGame().getScheduler().createTaskBuilder().interval(getTime(), TimeUnit.MILLISECONDS).name(name).execute(t -> {
    		for(Location<World> location : locations) {
    			ParticleEffect particle = ParticleEffect.builder().type(getType()).build();

    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    		}
        }).submit(Main.getPlugin());
	}
	
	private void spawnColoredRepeat(String name, List<Location<World>> locations, ThreadLocalRandom random, ParticleColor color) {
        Main.getGame().getScheduler().createTaskBuilder().interval(getTime(), TimeUnit.MILLISECONDS).name(name).execute(t -> {
    		for(Location<World> location : locations) {
    			ColoredParticle particle = ColoredParticle.builder().color(color.getColor()).type((Colorable) getType()).build();

    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    		}
        }).submit(Main.getPlugin());
	}
}
