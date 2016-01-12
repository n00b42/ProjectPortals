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

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

public class SignListener {
	
	@Listener
	public void onSignCreateEvent(ChangeSignEvent event, @First Player player) {
		SignData signData = event.getText();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		String title = lines.get(0).toPlain();
		
		if(!title.equalsIgnoreCase("[Portal]")) {
			return;
		}

		String worldName = Resource.getBaseName(lines.get(1).toPlain());
		String prettyWorldName = Resource.getPrettyName(worldName);
		
		if(!player.hasPermission("pjp.sign.place")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to place portal signs"));
			event.setCancelled(true);
			return;
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, prettyWorldName, " does not exist"));
			event.setCancelled(true);
			return;
		}

		lines.set(0, Text.of(TextColors.DARK_BLUE, "[Portal]"));
		lines.set(1, Text.of(prettyWorldName));
		
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

		Optional<SignData> optionalSignData = block.getOrCreate(SignData.class);
		if(!optionalSignData.isPresent()){
			return;
		}
		SignData signData = optionalSignData.get();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text title = Text.of(TextColors.DARK_BLUE, "[Portal]");
		
		if(!lines.get(0).equals(title)) {
        	return;
		}

		String worldName = Resource.getBaseName(lines.get(1).toPlain());
		String prettyWorldName = Resource.getPrettyName(worldName);
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, prettyWorldName, " does not exist"));
			block.remove(Keys.SIGN_LINES);
			event.setCancelled(true);
			return;
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		if(!player.hasPermission("pjp.sign.interact")) {
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to interact with portal signs"));
			event.setCancelled(true);
			return;
		}
		
		Location<World> spawnLocation;

		if(lines.size() < 3){
			spawnLocation = world.getSpawnLocation();
		}else{
			String coords = lines.get(2).toPlain();

			if(coords.equalsIgnoreCase("random")){	
				spawnLocation = Resource.getRandomLocation(world);
			}else{		
				spawnLocation = Resource.getLocation(world, coords);
			}
		}
		
		if(spawnLocation == null){
			player.sendMessage(Text.of(TextColors.DARK_RED, "Invalid coordinates"));
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
	    	Optional<Location<World>> optionalBlock = blockTransaction.getOriginal().getLocation();	
	    	
	    	if(!optionalBlock.isPresent()){
	    		continue;
	    	}
    		Location<World> block = optionalBlock.get();
    		
    		if(!(block.getBlock().getType().equals(BlockTypes.WALL_SIGN) && block.getBlock().getType().equals(BlockTypes.STANDING_SIGN))){
    			continue;
    		}	
			Optional<SignData> optionalSignData = block.getOrCreate(SignData.class);
			
			if(!optionalSignData.isPresent()){
				continue;
			}

			ListValue<Text> lines = optionalSignData.get().getValue(Keys.SIGN_LINES).get();

			Text title = Text.of(TextColors.DARK_BLUE, "[Portal]");
			if(!lines.get(0).equals(title)) {
				continue;
			}

			if(!player.hasPermission("pjp.sign.break")) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to break portal signs"));
				event.setCancelled(true);
				return;
			}
	    }
	}
}
