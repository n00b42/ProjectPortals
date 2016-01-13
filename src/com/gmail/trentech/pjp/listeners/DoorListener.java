package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Door;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

public class DoorListener {

	public static HashMap<Player, String> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		

			if(!Door.get(location).isPresent()){
				continue;
			}
			
			if(!player.hasPermission("pjp.door.break")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to break door portals"));
				event.setCancelled(true);
			}else{
				Door.remove(location);
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke door portal"));
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @First Player player) {
		if(!creators.containsKey(player)){
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockType type = transaction.getFinal().getState().getType();
			
			if(!type.equals(BlockTypes.ACACIA_DOOR) && !type.equals(BlockTypes.BIRCH_DOOR) && !type.equals(BlockTypes.DARK_OAK_DOOR)
					 && !type.equals(BlockTypes.IRON_DOOR) && !type.equals(BlockTypes.JUNGLE_DOOR) && !type.equals(BlockTypes.SPRUCE_DOOR)
					 && !type.equals(BlockTypes.TRAPDOOR) && !type.equals(BlockTypes.WOODEN_DOOR)){
				continue;
			}

			Location<World> location = transaction.getFinal().getLocation().get();

			if(!player.hasPermission("pjp.door.place")){
	        	player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place door portals"));
	        	creators.remove(player);
	        	event.setCancelled(true);
	        	return;
			}

            String destination = creators.get(player);
            
            Door.save(location, destination);

    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Resource.spawnParticles(location, 1.0, false);
    		}

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "New door portal created"));
            
            creators.remove(player);
		}
	}
	
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event){
		if (!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();

		Location<World> location = player.getLocation();		

		if(!Door.get(location).isPresent()){
			return;
		}

		Door door = Door.get(location).get();

		if(!player.hasPermission("pjp.lever.interact")){
			player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with door portals"));
			event.setCancelled(true);
			return;
		}
		
		if(!door.getDestination().isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, "World does not exist"));
			return;
		}
		Location<World> spawnLocation = door.getDestination().get();

		TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of("door"));

		if(!Main.getGame().getEventManager().post(teleportEvent)){
			spawnLocation = teleportEvent.getDestination();
			player.setLocation(spawnLocation);
		}
	}
}
