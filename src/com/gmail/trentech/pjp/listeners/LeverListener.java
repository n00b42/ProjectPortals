package com.gmail.trentech.pjp.listeners;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent.TargetPlayer;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Lever;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Particle;

public class LeverListener {

	public static ConcurrentHashMap<Player, String> builders = new ConcurrentHashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();
			BlockType blockType = snapshot.getState().getType();
			
			if(!blockType.equals(BlockTypes.LEVER)){
				continue;
			}

			Location<World> location = snapshot.getLocation().get();		

			Optional<Lever> optionalLever = Lever.get(location);
			
			if(!optionalLever.isPresent()){
				continue;
			}
			
			Lever lever = optionalLever.get();

			if(new ConfigManager().getConfig().getNode("options", "portal_permissions").getBoolean()){
				if(!player.hasPermission("pjp.lever." + location.getExtent().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ())){
					player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to use this lever portal"));
					event.setCancelled(true);
					return;
				}
			}else{
				if(!player.hasPermission("pjp.lever.interact")){
					player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with lever portals"));
					event.setCancelled(true);
					return;
				}
			}
			
			Optional<Location<World>> optionalSpawnLocation = lever.getDestination();
			
			if(!optionalSpawnLocation.isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, "World does not exist"));
				continue;
			}
			Location<World> spawnLocation = optionalSpawnLocation.get();

			TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of(lever));

			if(!Main.getGame().getEventManager().post(teleportEvent)){
				Location<World> currentLocation = player.getLocation();
				spawnLocation = teleportEvent.getDestination();
				
				Optional<Vector3d> optionalRotation = lever.getRotation();
				
				if(optionalRotation.isPresent()){
					player.setLocationAndRotation(spawnLocation, optionalRotation.get());
				}else{
					player.setLocation(spawnLocation);
				}
				
				TargetPlayer displaceEvent = SpongeEventFactory.createDisplaceEntityEventTargetPlayer(Cause.of(this), new Transform<World>(currentLocation), new Transform<World>(spawnLocation), player);
				Main.getGame().getEventManager().post(displaceEvent);
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
		return;
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

    		Particle.spawnParticle(location, new ConfigManager().getConfig().getNode("options", "particles", "type", "creation").getString());

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "New button lever created"));
            
            builders.remove(player);
		}
	}
}
