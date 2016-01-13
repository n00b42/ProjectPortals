package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Plate;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

public class PlateListener {

	public static HashMap<Player, String> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot block = transaction.getFinal();
			BlockType type = block.getState().getType();
			
			if(!type.equals(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE) && !type.equals(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE) 
					&& !type.equals(BlockTypes.STONE_PRESSURE_PLATE) && !type.equals(BlockTypes.WOODEN_PRESSURE_PLATE)){
				return;
			}

			if(!block.getExtendedState().get(Keys.POWERED).isPresent()){
				return;
			}

			if(!block.getExtendedState().get(Keys.POWERED).get()){
				return;
			}

			Location<World> location = block.getLocation().get();		

			if(!Plate.get(location).isPresent()){
				return;
			}	

			Plate plate = Plate.get(location).get();
			
			if(!plate.getDestination().isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, "World does not exist"));
				return;
			}
			Location<World> spawnLocation = plate.getDestination().get();

			TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of("plate"));

			if(!Main.getGame().getEventManager().post(teleportEvent)){
				spawnLocation = teleportEvent.getDestination();
				player.setLocation(spawnLocation);
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		if(creators.containsKey(player)){
			creators.remove(player);
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		

			if(!Plate.get(location).isPresent()){
				continue;
			}
			
			if(!player.hasPermission("pjp.plate.break")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to break pressure plate portals"));
				event.setCancelled(true);
			}else{
				Plate.remove(location);
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke pressure plate portal"));
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
			
			if(!type.equals(BlockTypes.STONE_BUTTON) && !type.equals(BlockTypes.WOODEN_BUTTON)){
				continue;
			}

			Location<World> location = transaction.getFinal().getLocation().get();

			if(!player.hasPermission("pjp.plate.place")){
	        	player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place pressure plate portals"));
	        	creators.remove(player);
	        	event.setCancelled(true);
	        	return;
			}

            String destination = creators.get(player);
            
            Plate.save(location, destination);

    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Resource.spawnParticles(location, 1.0, false);
    		}

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "New pressure plate portal created"));
            
            creators.remove(player);
		}
	}
}
