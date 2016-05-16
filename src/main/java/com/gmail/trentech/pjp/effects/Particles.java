package com.gmail.trentech.pjp.effects;

import java.util.Optional;

import org.spongepowered.api.effect.particle.ParticleTypes;

public enum Particles {

	PORTAL(new Particle("PORTAL", ParticleTypes.PORTAL, 10, false)), FLAME(new Particle("FLAME", ParticleTypes.FLAME, 40, false)),
	CLOUD(new Particle("CLOUD", ParticleTypes.CLOUD, 60, false)), HEART(new Particle("HEART", ParticleTypes.HEART, 120, false)),
	SMOKE_LARGE(new Particle("SMOKE_LARGE", ParticleTypes.SMOKE_LARGE, 60, false)), ENCHANTMENT_TABLE(new Particle("ENCHANTMENT_TABLE", ParticleTypes.ENCHANTMENT_TABLE, 10, false)),
	VILLAGER_HAPPY(new Particle("VILLAGER_HAPPY", ParticleTypes.VILLAGER_HAPPY, 50, false)), SPELL_WITCH(new Particle("SPELL_WITCH", ParticleTypes.SPELL_WITCH, 15, false)),
	NOTE(new Particle("NOTE", ParticleTypes.NOTE, 80, false)), REDSTONE(new Particle("REDSTONE", ParticleTypes.REDSTONE, 15, true)),
	SPELL(new Particle("SPELL", ParticleTypes.SPELL, 15, false)), WATER_BUBBLE(new Particle("WATER_BUBBLE", ParticleTypes.WATER_BUBBLE, 5, false)),
	CRIT_MAGIC(new Particle("CRIT_MAGIC", ParticleTypes.CRIT_MAGIC, 15, false)), CRIT(new Particle("CRIT", ParticleTypes.CRIT, 15, false)),
	SNOWBALL(new Particle("SNOWBALL", ParticleTypes.SNOWBALL, 15, false)), SLIME(new Particle("SLIME", ParticleTypes.SLIME, 15, false)),
	SNOW_SHOVEL(new Particle("SNOW_SHOVEL", ParticleTypes.SNOW_SHOVEL, 10, false)), SUSPENDED_DEPTH(new Particle("SUSPENDED_DEPTH", ParticleTypes.SUSPENDED_DEPTH, 5, false)),
	VILLAGER_ANGRY(new Particle("VILLAGER_ANGRY", ParticleTypes.VILLAGER_ANGRY, 40, false)), WATER_SPLASH(new Particle("WATER_SPLASH", ParticleTypes.WATER_SPLASH, 7, false)),
	WATER_WAKE(new Particle("WATER_WAKE", ParticleTypes.WATER_WAKE, 7, false)), WATER_DROP(new Particle("WATER_DROP", ParticleTypes.WATER_DROP, 7, false));

	private final Particle particle;

	private Particles(Particle particle) {
		this.particle = particle;
	}

	public static Optional<Particle> get(String name) {
    	Optional<Particle> optional = Optional.empty();
    	
    	Particles[] particles = Particles.values();
    	
        for (Particles particle : particles) {
        	if(particle.particle.getName().equals(name)) {
        		optional = Optional.of(particle.particle);
        		break;
        	}    		
        }
        
        return optional;
    }
}
