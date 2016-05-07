package com.gmail.trentech.pjp.utils;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent.TargetPlayer;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;

public class Utils {

	public static Location<World> getRandomLocation(World world) {
		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		Location<World> location = world.getSpawnLocation();

		long radius = new ConfigManager().getConfig().getNode("options", "random_spawn_radius").getLong();
		
		for(int i = 0; i < 49; i++){
			int x = (int) (random.nextDouble() * ((radius*2) + 1) - radius);
			int y = random.nextInt(64, 200 + 1);
			int z = (int) (random.nextDouble() * ((radius*2) + 1) - radius);
			
			Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(world.getLocation(x, y, z));

			if(!optionalLocation.isPresent()){
				continue;
			}
			Location<World> unsafeLocation = optionalLocation.get();
			
			BlockType blockType = unsafeLocation.getBlockType();
			
			if(!blockType.equals(BlockTypes.AIR) || !unsafeLocation.getRelative(Direction.UP).getBlockType().equals(BlockTypes.AIR)){
				continue;
			}
			
			BlockType floorBlockType = unsafeLocation.getRelative(Direction.DOWN).getBlockType();
			
			if(floorBlockType.equals(BlockTypes.WATER) || floorBlockType.equals(BlockTypes.LAVA) || floorBlockType.equals(BlockTypes.FLOWING_WATER) 
					|| floorBlockType.equals(BlockTypes.FLOWING_LAVA) || floorBlockType.equals(BlockTypes.FIRE)){
				continue;
			}
			
			location = unsafeLocation;
			break;
		}
		return location;
	}

	public static Consumer<CommandSource> unsafeTeleport(Location<World> location){
		return (CommandSource src) -> {
			Player player = (Player)src;
			
			Location<World> currentLocation = player.getLocation();

			player.setLocation(location);

			TargetPlayer displaceEvent = SpongeEventFactory.createDisplaceEntityEventTargetPlayer(Cause.of(NamedCause.source(player)), new Transform<World>(currentLocation), new Transform<World>(location), player);
			Main.getGame().getEventManager().post(displaceEvent);
		};
	}
	
	public static Optional<Location<World>> getDestination(String destination) {
		String[] args = destination.split(":");
		
		Optional<World> optional = Main.getGame().getServer().getWorld(args[0]);
		
		if(!optional.isPresent()){
			return Optional.empty();
		}
		World world = optional.get();
		
		if(args[1].equalsIgnoreCase("random")){
			return Optional.of(Utils.getRandomLocation(world));
		}else if(args[1].equalsIgnoreCase("spawn")){
			return Optional.of(world.getSpawnLocation());
		}else{
			String[] coords = args[1].split("\\.");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			return Optional.of(world.getLocation(x, y, z));	
		}
	}
	
	public static Optional<Vector3d> getRotation(String destination){
		String[] args = destination.split(":");
		
		if(args.length != 3){
			return Optional.empty();
		}
		
		Optional<Rotation> optional = Rotation.get(args[2]);
		
		if(!optional.isPresent()){
			return Optional.empty();
		}
		
		return Optional.of(new Vector3d(0,optional.get().getValue(),0));
	}
}
