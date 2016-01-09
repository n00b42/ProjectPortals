package com.gmail.trentech.pjp;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class Resource {

	public final static String NAME = "Project Portals";
	public final static String VERSION = "0.4.13";
	public final static String ID = "PJP";
	
	private static HashMap<World, Location<World>> randomLocations = new HashMap<>();

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
	
	public static String getPrettyName(String worldName){
		if(worldName.equalsIgnoreCase("DIM-1")){
			return "nether";
		}else if(worldName.equalsIgnoreCase("DIM1")){
			return "end";
		}else{
			return worldName;
		}
	}
	
	public static String getBaseName(String prettyWorldName){
		if(prettyWorldName.equalsIgnoreCase("nether")){
			return "DIM-1";
		}else if(prettyWorldName.equalsIgnoreCase("end")){
			return "DIM1";
		}else{
			return prettyWorldName;
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
	
	private static Location<World> generate(World world, long radius){
		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		int x = (int) (random.nextDouble() * ((radius*2) + 1) - radius);
		int y = random.nextInt(64, 200 + 1);
		int z = (int) (random.nextDouble() * ((radius*2) + 1) - radius);

		Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(world.getLocation(x, y, z));

		if(!optionalLocation.isPresent()){
			return generate(world, radius);
		}
		Location<World> location = optionalLocation.get();
		
		if(!location.getBlockType().equals(BlockTypes.AIR) || !location.getRelative(Direction.UP).getBlockType().equals(BlockTypes.AIR)){
			return generate(world, radius);
		}
		
		Location<World> floor = location.getRelative(Direction.DOWN);
		if(floor.getBlockType().equals(BlockTypes.WATER) 
				|| floor.getBlockType().equals(BlockTypes.LAVA)
				|| floor.getBlockType().equals(BlockTypes.FLOWING_WATER)
				|| floor.getBlockType().equals(BlockTypes.FLOWING_LAVA)
				|| floor.getBlockType().equals(BlockTypes.FIRE)){
			return generate(world, radius);
		}
		
		return location;
	}

	public static void generateRandomLocation(World world){
		randomLocations.put(world, generate(world, new ConfigManager().getConfig().getNode("Options", "Random-Spawn-Radius").getLong()));
	}
	
	public static Location<World> getRandomLocation(World world) {
		if(randomLocations.get(world) == null){
			generateRandomLocation(world);
		}
		
		return randomLocations.get(world);
	}
}
