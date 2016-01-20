package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.mutable.PortalData;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

public class SignListener {
	
	public static HashMap<Player, PortalData> builders = new HashMap<>();
	
	@Listener
	public void onSignCreateEvent(ChangeSignEvent event, @First Player player) {
		if(!builders.containsKey(player)){
			return;
		}
		PortalData portalData = builders.get(player);
		
		if(!player.hasPermission("pjp.sign.place")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to place sign portals"));
			event.setCancelled(true);
			return;
		}
		
		Sign sign = event.getTargetTile();
		
		sign.offer(portalData);

		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Utils.spawnParticles(event.getTargetTile().getLocation(), 1.0, false);
		}
		
        player.sendMessage(Text.of(TextColors.DARK_GREEN, "New sign portal created"));
        
        builders.remove(player);
	}

	@Listener
	public void onSignInteractEvent(InteractBlockEvent.Secondary event, @First Player player) {
		BlockSnapshot snapshot = event.getTargetBlock();
		if(!(snapshot.getState().getType().equals(BlockTypes.WALL_SIGN) || snapshot.getState().getType().equals(BlockTypes.STANDING_SIGN))){
			return;
		}

		Location<World> location = snapshot.getLocation().get();

		Optional<PortalData> optionalPortalData = location.get(PortalData.class);
		
		if(!optionalPortalData.isPresent()){
			return;
		}
		PortalData portalData = optionalPortalData.get();

		Optional<Location<World>> optionalSpawnLocation = portalData.getDestination();
		
		if(!optionalSpawnLocation.isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, portalData.destination().get().split(":")[0], " does not exist"));
			location.remove(Keys.SIGN_LINES);
			return;
		}
		Location<World> spawnLocation = optionalSpawnLocation.get();
		
		if(!player.hasPermission("pjp.sign.interact")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to interact with sign portals"));
			event.setCancelled(true);
			return;
		}

		TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of("sign"));

		if(!Main.getGame().getEventManager().post(teleportEvent)){
			spawnLocation = teleportEvent.getDestination();
			player.setLocation(spawnLocation);
		}
	}
	
	@Listener
	public void onSignBreakEvent(ChangeBlockEvent.Break event, @First Player player) {
	    for(Transaction<BlockSnapshot> blockTransaction : event.getTransactions()){
	    	Optional<Location<World>> optionalLocation = blockTransaction.getOriginal().getLocation();	
	    	
	    	if(!optionalLocation.isPresent()){
	    		continue;
	    	}
    		Location<World> location = optionalLocation.get();
    		
    		BlockType blockType = location.getBlock().getType();
    		
    		if(!blockType.equals(BlockTypes.WALL_SIGN) && !blockType.equals(BlockTypes.STANDING_SIGN)){
    			continue;
    		}
    		
			Optional<PortalData> optionalPortalData = location.get(PortalData.class);
			
			if(!optionalPortalData.isPresent()){
				continue;
			}

			if(!player.hasPermission("pjp.sign.break")) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to break sign portal"));
				event.setCancelled(true);
			}else{
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke sign portal"));
			}
	    }
	}
}
