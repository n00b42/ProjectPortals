package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
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
import com.gmail.trentech.pjp.data.portal.PortalData;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

public class SignListener {
	
	public static HashMap<Player, PortalData> creators = new HashMap<>();
	
	@Listener
	public void onSignCreateEvent(ChangeSignEvent event, @First Player player) {
		if(!creators.containsKey(player)){
			return;
		}
		PortalData portalData = creators.get(player);
		
		if(!player.hasPermission("pjp.sign.place")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to place portal signs"));
			event.setCancelled(true);
			return;
		}
		
		Sign sign = event.getTargetTile();
		
		sign.offer(portalData);

		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Utils.spawnParticles(event.getTargetTile().getLocation(), 1.0, false);
		}
	}

	@Listener
	public void onSignInteractEvent(InteractBlockEvent.Secondary event, @First Player player) {
		if(!(event.getTargetBlock().getState().getType().equals(BlockTypes.WALL_SIGN) || event.getTargetBlock().getState().getType().equals(BlockTypes.STANDING_SIGN))){
			return;
		}

		Location<World> block = event.getTargetBlock().getLocation().get();

		Optional<SignData> optionalSignData = block.getOrCreate(SignData.class);
		if(!optionalSignData.isPresent()){
			return;
		}

		Optional<PortalData> optionalPortalData = block.getOrCreate(PortalData.class);
		if(!optionalPortalData.isPresent()){
			return;
		}
		PortalData portalData = optionalPortalData.get();

		if(!portalData.getDestination().isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, portalData.destination().get().split(":")[0], " does not exist"));
			block.remove(Keys.SIGN_LINES);
			event.setCancelled(true);
			return;
		}
		Location<World> destination = portalData.getDestination().get();
		
		if(!player.hasPermission("pjp.sign.interact")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to interact with portal signs"));
			event.setCancelled(true);
			return;
		}

		TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), destination, Cause.of("sign"));

		if(!Main.getGame().getEventManager().post(teleportEvent)){
			destination = teleportEvent.getDestination();
			player.setLocation(destination);
		}
	}
	
	@Listener
	public void onSignBreakEvent(ChangeBlockEvent.Break event, @First Player player) {
	    for(Transaction<BlockSnapshot> blockTransaction : event.getTransactions()){
	    	Optional<Location<World>> optionalBlock = blockTransaction.getOriginal().getLocation();	
	    	
	    	if(!optionalBlock.isPresent()){
	    		continue;
	    	}
    		Location<World> block = optionalBlock.get();
    		
    		if(!(block.getBlock().getType().equals(BlockTypes.WALL_SIGN) && block.getBlock().getType().equals(BlockTypes.STANDING_SIGN))){
    			continue;
    		}	
			Optional<PortalData> optionalPortalData = block.getOrCreate(PortalData.class);
			
			if(!optionalPortalData.isPresent()){
				continue;
			}

			if(!player.hasPermission("pjp.sign.break")) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to break portal signs"));
				event.setCancelled(true);
			}else{
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke button portal"));
			}
	    }
	}
}
