package com.gmail.trentech.pjp;

import java.util.Random;

import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class Resource {

	public final static String NAME = "Project Portals";
	public final static String VERSION = "0.3.6";
	public final static String ID = "Project Portals";

	public static void spawnParticles(Location<World> location, double range, boolean sub){
		
		Random random = new Random();
		
		for(int i = 0; i < 5; i++){
			double v1 = 0.0 + (range - 0.0) * random.nextDouble();
			double v2 = 0.0 + (range - 0.0) * random.nextDouble();
			double v3 = 0.0 + (range - 0.0) * random.nextDouble();

			location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
					.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(v3,v1,v2));
			location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
					.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,v1,0));
			if(sub){
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().sub(v1,v2,v3));
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().sub(0,v2,0));
			}else{
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(v3,v1,v1));
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(v2,v3,v2));
			}
		}
	}
	
	public static Location<World> getLocation(World world, String coords){
		String[] array = coords.split(" ");
		
		int x;
		int y;
		int z;

		try{
			x = Integer.parseInt(array[0]);
			y = Integer.parseInt(array[1]);
			z = Integer.parseInt(array[2]);
		}catch(Exception e){
			return null;
		}
		
		return world.getLocation(x, y, z);
	}
}
