package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;
import java.util.Optional;

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

	public static HashMap<Player, String> builders = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();
			BlockType blockType = snapshot.getState().getType();
			
			if(!blockType.equals(BlockTypes.LEVER)){
				return;
			}

			Location<World> location = snapshot.getLocation().get();		

			Optional<Lever> optionalLever = Lever.get(location);
			
			if(!optionalLever.isPresent()){
				return;
			}
			
			Lever lever = optionalLever.get();

			if(!player.hasPermission("pjp.lever.interact")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with lever portals"));
				event.setCancelled(true);
				return;
			}
			
			Optional<Location<World>> optionalSpawnLocation = lever.getDestination();
			
			if(!optionalSpawnLocation.isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, "World does not exist"));
				return;
			}
			Location<World> spawnLocation = optionalSpawnLocation.get();

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
		if(!builders.containsKey(player)){
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockType blockType = transaction.getFinal().getState().getType();
			
			if(!blockType.equals(BlockTypes.LEVER)){
				continue;
			}

			Location<World> location = transaction.getFinal().getLocation().get();

			if(!player.hasPermission("pjp.lever.place")){
	        	player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place lever portals"));
	        	builders.remove(player);
	        	event.setCancelled(true);
	        	return;
			}

            String destination = builders.get(player);
            
            Lever.save(location, destination);

    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Utils.spawnParticles(location, 1.0, false);
    		}

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "New button lever created"));
            
            builders.remove(player);
		}
	}
}
