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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;

public class Utils {

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

		try{
			int x = Integer.parseInt(array[0]);
			int y = Integer.parseInt(array[1]);
			int z = Integer.parseInt(array[2]);
			
			return world.getLocation(x, y, z);
		}catch(Exception e){
			return null;
		}
	}

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
			player.sendTitle(Title.of(Text.of(TextColors.GOLD, getPrettyName(location.getExtent().getName())), Text.of(TextColors.DARK_PURPLE, "x: ", location.getBlockX(), ", y: ", location.getBlockY(),", z: ", location.getBlockZ())));
		
			TargetPlayer displaceEvent = SpongeEventFactory.createDisplaceEntityEventTargetPlayer(Cause.of(NamedCause.source(player)), new Transform<World>(currentLocation), new Transform<World>(location), player);
			Main.getGame().getEventManager().post(displaceEvent);
		};
	}
	

}
