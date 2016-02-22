package com.gmail.trentech.pjp.utils;

import java.util.Optional;

import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;

public enum Particles {

	PORTAL("PORTAL", ParticleTypes.PORTAL, 10, false), FLAME("FLAME", ParticleTypes.FLAME, 40, false),
	CLOUD("CLOUD", ParticleTypes.CLOUD, 60, false), HEART("HEART", ParticleTypes.HEART, 75, false),
	SMOKE_LARGE("SMOKE_LARGE", ParticleTypes.SMOKE_LARGE, 60, false), ENCHANTMENT_TABLE("ENCHANTMENT_TABLE", ParticleTypes.ENCHANTMENT_TABLE, 10, false),
	VILLAGER_HAPPY("VILLAGER_HAPPY", ParticleTypes.VILLAGER_HAPPY, 50, false), SPELL_WITCH("SPELL_WITCH", ParticleTypes.SPELL_WITCH, 15, false),
	NOTE("NOTE", ParticleTypes.NOTE, 80, false), REDSTONE("REDSTONE", ParticleTypes.REDSTONE, 10, true),
	SPELL("SPELL", ParticleTypes.SPELL, 10, false), WATER_BUBBLE("WATER_BUBBLE", ParticleTypes.WATER_BUBBLE, 5, false),
	CRIT_MAGIC("CRIT_MAGIC", ParticleTypes.CRIT_MAGIC, 10, false), CRIT("CRIT", ParticleTypes.CRIT, 10, false),
	SNOWBALL("SNOWBALL", ParticleTypes.SNOWBALL, 10, false), SLIME("SLIME", ParticleTypes.SLIME, 10, false),
	SNOW_SHOVEL("SNOW_SHOVEL", ParticleTypes.SNOW_SHOVEL, 10, false), SUSPENDED_DEPTH("SUSPENDED_DEPTH", ParticleTypes.SUSPENDED_DEPTH, 5, false),
	VILLAGER_ANGRY("VILLAGER_ANGRY", ParticleTypes.VILLAGER_ANGRY, 40, false), WATER_SPLASH("WATER_SPLASH", ParticleTypes.WATER_SPLASH, 7, false),
	WATER_WAKE("WATER_WAKE", ParticleTypes.WATER_WAKE, 7, false), WATER_DROP("WATER_DROP", ParticleTypes.WATER_DROP, 7, false);
	
	private final String name;
	private final ParticleType type;
	private final long time;
	private final boolean colored;
	
	private Particles(String name, ParticleType type, long time, boolean colored){
		this.name = name;
		this.type = type;
		this.time = time;
		this.colored = colored;
	}
	
    public String getName() {
		return name;
	}

	public ParticleType getType() {
		return type;
	}

	public long getTime() {
		return time;
	}

	public boolean isColored(){
		return colored;
	}
	
	public static Optional<Particles> get(String name){
    	Optional<Particles> optional = Optional.empty();
    	
    	Particles[] particles = Particles.values();
    	
        for (Particles particle : particles){
        	if(particle.getName().equals(name)){
        		optional = Optional.of(particle);
        		break;
        	}    		
        }
        
        return optional;
    }
}
