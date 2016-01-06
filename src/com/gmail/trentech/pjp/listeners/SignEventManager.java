package com.gmail.trentech.pjp.listeners;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
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

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.Resource;
import com.gmail.trentech.pjp.events.TeleportEvent;

public class SignEventManager {
	
	@Listener
	public void onSignCreateEvent(ChangeSignEvent event, @First Player player) {
		SignData signData = event.getText();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text portalSign = Text.of("[Portal]");
		if(!lines.get(0).equals(portalSign)) {
			return;
		}

		if(!player.hasPermission("pjp.sign.place")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to place portal signs"));
			event.setCancelled(true);
			return;
		}
		
		if(!Main.getGame().getServer().getWorld(lines.get(1).toPlain()).isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, lines.get(1).toPlain(), " does not exist"));
			event.setCancelled(true);
			return;
		}

		lines.set(0, Text.of(TextColors.DARK_BLUE, "[Portal]"));

		event.getText().set(lines);
		
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Resource.spawnParticles(event.getTargetTile().getLocation(), 1.0, false);
		}
	}

	@Listener
	public void onSignInteractEvent(InteractBlockEvent.Secondary event, @First Player player) {
		if(!(event.getTargetBlock().getState().getType().equals(BlockTypes.WALL_SIGN) || event.getTargetBlock().getState().getType().equals(BlockTypes.STANDING_SIGN))){
			return;
		}

		Location<World> block = event.getTargetBlock().getLocation().get();

		Optional<SignData> data = block.getOrCreate(SignData.class);
		if(!data.isPresent()){
			return;
		}
		SignData signData = data.get();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text portalSign = Text.of(TextColors.DARK_BLUE, "[Portal]");
		if(!lines.get(0).equals(portalSign)) {
        	return;
		}

		if(!Main.getGame().getServer().getWorld(lines.get(1).toPlain()).isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, lines.get(1).toPlain(), " does not exist"));
			event.setCancelled(true);
			return;
		}		
		World world = Main.getGame().getServer().getWorld(lines.get(1).toPlain()).get();
		
		if(!player.hasPermission("pjp.sign.interact")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to interact with portal signs"));
			event.setCancelled(true);
			return;
		}
		
		Main.getGame().getEventManager().post(new TeleportEvent(player.getLocation(), world.getSpawnLocation(), Cause.of(player)));
	}
	
	@Listener
	public void onSignBreakEvent(ChangeBlockEvent.Break event, @First Player player) {
	    SignData signData = null;
	    
	    for(Transaction<BlockSnapshot> blockTransaction : event.getTransactions()){
	    	Optional<Location<World>> blockOptional = blockTransaction.getOriginal().getLocation();	
	    	
	    	if(!blockOptional.isPresent()){
	    		continue;
	    	}
    		Location<World> block = blockOptional.get();
    		
    		if(!(block.getBlock().getType().equals(BlockTypes.WALL_SIGN) && block.getBlock().getType().equals(BlockTypes.STANDING_SIGN))){
    			continue;
    		}	
			Optional<SignData> signDataOptional = block.getOrCreate(SignData.class);
			
			if(signDataOptional.isPresent()){
				signData = signDataOptional.get();
				break;
			}
	    }

	    if(signData == null){
	    	return;
	    }
	    
		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text kitSign = Text.of(TextColors.DARK_BLUE, "[Portal]");
		if(!lines.get(0).equals(kitSign)) {
        	return;
		}

		if(!player.hasPermission("pjp.sign.break")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to break portal signs"));
			event.setCancelled(true);
			return;
		}
	}
}
