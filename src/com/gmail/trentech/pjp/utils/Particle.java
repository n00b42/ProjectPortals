package com.gmail.trentech.pjp.utils;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleType.Colorable;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.portals.Portal;

public class Particle {

	public static void spawnParticle(Location<World> location, String part){
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()){
			
			
			String[] split = part.split(":");

			Optional<Particles> optionalParticleType = Particles.get(split[0]);
			
			if(!optionalParticleType.isPresent()){
				return;
			}
			
			Particles particle = optionalParticleType.get();

			if(split.length == 2 && particle.isColored()){
				Optional<Color> optionalColor = getColor(split[1]);
				
				if(optionalColor.isPresent()){
					spawnColoredNonRepeat(location, (Colorable) particle.getType(), split[1]);
					return;
				}
			}
			
			spawnNonRepeat(location, particle.getType());
		}
	}
	
	private static void spawnNonRepeat(Location<World> location, ParticleType type){
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for(int i = 0; i < 9; i++){	
			ParticleEffect particle = ParticleEffect.builder().type(type).build();
			
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
		}
	}
	
	private static void spawnColoredNonRepeat(Location<World>location, Colorable type, String color){
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for(int i = 0; i < 9; i++){	
			ColoredParticle particle = ColoredParticle.builder().color(getColor(color).get()).type(type).build();
			
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
		}
	}
	
	public static void createTask(Portal portal){
		if(new ConfigManager().getConfig().getNode("options", "particles", "enable").getBoolean()){
			ThreadLocalRandom random = ThreadLocalRandom.current();

			String[] p = portal.getParticle().split(":");
			
			Optional<Particles> optionalParticle = Particles.get(p[0]);
			
			if(!optionalParticle.isPresent()){
				return;
			}
			Particles particle = optionalParticle.get();
			
			if(p.length == 2 && particle.isColored()){
				spawnColoredRepeat(portal.getName(), portal.getFill(), random, (Colorable) particle.getType(), p[1], particle.getTime());
			}else{
				spawnRepeat(portal.getName(), portal.getFill(), random, particle.getType(), particle.getTime());
			}
		}
	}

	private static void spawnRepeat(String name, List<Location<World>> locations, ThreadLocalRandom random, ParticleType type, long time){
        Main.getGame().getScheduler().createTaskBuilder().interval(time, TimeUnit.MILLISECONDS).name(name).execute(t -> {
    		for(Location<World> location : locations){
    			ParticleEffect particle = ParticleEffect.builder().type(type).build();

    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    		}
        }).submit(Main.getPlugin());
	}
	
	private static void spawnColoredRepeat(String name, List<Location<World>> locations, ThreadLocalRandom random, Colorable type, String color, long time){
        Main.getGame().getScheduler().createTaskBuilder().interval(time, TimeUnit.MILLISECONDS).name(name).execute(t -> {
    		for(Location<World> location : locations){
    			ColoredParticle particle = ColoredParticle.builder().color(getColor(color).get()).type(type).build();

    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    			location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(),random.nextDouble(),random.nextDouble()));
    		}
        }).submit(Main.getPlugin());
	}
	
	public static Optional<Color> getColor(String name){
		switch(name){
			case "BLACK": return Optional.of(Color.BLACK);
			case "BLUE": return Optional.of(Color.BLUE);
			case "CYAN": return Optional.of(Color.CYAN);
			case "DARK_CYAN": return Optional.of(Color.DARK_CYAN);
			case "DARK_GREEN": return Optional.of(Color.DARK_GREEN);
			case "DARK_MAGENTA": return Optional.of(Color.DARK_MAGENTA);
			case "GRAY": return Optional.of(Color.GRAY);
			case "GREEN": return Optional.of(Color.GREEN);
			case "LIME": return Optional.of(Color.LIME);
			case "MAGENTA": return Optional.of(Color.MAGENTA);
			case "NAVY": return Optional.of(Color.NAVY);
			case "PINK": return Optional.of(Color.PINK);
			case "PURPLE": return Optional.of(Color.PURPLE);
			case "RED": return Optional.of(Color.RED);
			case "WHITE": return Optional.of(Color.WHITE);
			case "YELLOW": return Optional.of(Color.YELLOW);
			case "RAINBOW":
				int random = ThreadLocalRandom.current().nextInt(8 - 1 + 1) + 1;
				
				switch(random){
					case 1: return Optional.of(Color.BLUE);
					case 2: return Optional.of(Color.CYAN);
					case 3: return Optional.of(Color.LIME);
					case 4: return Optional.of(Color.MAGENTA);
					case 5: return Optional.of(Color.PINK);
					case 6: return Optional.of(Color.PURPLE);
					case 7: return Optional.of(Color.RED);
					case 8: return Optional.of(Color.YELLOW);
				}
			default: return Optional.empty();
		}
	}
}
