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
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Lever;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

public class LeverListener {

	public static HashMap<Player, String> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot block = transaction.getFinal();
			BlockType type = block.getState().getType();
			
			if(!type.equals(BlockTypes.LEVER)){
				return;
			}

			Location<World> location = block.getLocation().get();		

			if(!Lever.get(location).isPresent()){
				return;
			}
			
			Lever lever = Lever.get(location).get();

			if(!player.hasPermission("pjp.lever.interact")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with lever portals"));
				event.setCancelled(true);
				return;
			}
			
			if(!lever.getDestination().isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, "World does not exist"));
				return;
			}
			Location<World> spawnLocation = lever.getDestination().get();

			TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of("lever"));

			if(!Main.getGame().getEventManager().post(teleportEvent)){
				spawnLocation = teleportEvent.getDestination();
				player.setLocation(spawnLocation);
			}
		}
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		

			if(!Lever.get(location).isPresent()){
				continue;
			}
			
			if(!player.hasPermission("pjp.lever.break")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to break lever portals"));
				event.setCancelled(true);
			}else{
				Lever.remove(location);
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke lever portal"));
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
			
			if(!type.equals(BlockTypes.LEVER)){
				continue;
			}

			Location<World> location = transaction.getFinal().getLocation().get();

			if(!player.hasPermission("pjp.lever.place")){
	        	player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place lever portals"));
	        	creators.remove(player);
	        	event.setCancelled(true);
	        	return;
			}

            String destination = creators.get(player);
            
            Lever.save(location, destination);

    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Utils.spawnParticles(location, 1.0, false);
    		}

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "New button lever created"));
            
            creators.remove(player);
		}
	}
}
