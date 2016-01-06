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

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.Resource;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Button;

import ninja.leaping.configurate.ConfigurationNode;

public class ButtonEventManager {

	public static HashMap<Player, Button> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot block = transaction.getFinal();
			BlockType type = block.getState().getType();
			
			if(!(type.equals(BlockTypes.STONE_BUTTON) && type.equals(BlockTypes.STONE_BUTTON))){
				return;
			}

			if(!block.get(Keys.POWERED).isPresent()){
				return;
			}

			if(!block.get(Keys.POWERED).get()){
				return;
			}
			
			if(!player.hasPermission("pjp.button.interact")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with button portals"));
				return;
			}
			
			Location<World> location = block.getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	        ConfigurationNode config = new ConfigManager("portals.conf").getConfig();

			if(config.getNode("Buttons", locationName, "World").getString() == null){
				return;
			}
			String worldName = config.getNode("Buttons", locationName, "World").getString();
			
			if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
				return;
			}
			World world = Main.getGame().getServer().getWorld(worldName).get();
			
			int x = world.getSpawnLocation().getBlockX();
			int y = world.getSpawnLocation().getBlockY();
			int z = world.getSpawnLocation().getBlockZ();
			
			if(config.getNode("Buttons", locationName, "X").getString() != null && config.getNode("Buttons", locationName, "Y").getString() != null && config.getNode("Buttons", locationName, "Z").getString() != null){
				x = config.getNode("Buttons", locationName, "X").getInt();
				y = config.getNode("Buttons", locationName, "Y").getInt();
				z = config.getNode("Buttons", locationName, "Z").getInt();
			}

			Main.getGame().getEventManager().post(new TeleportEvent(player.getLocation(), world.getLocation(x, y, z), Cause.of(player)));
		}
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		if(creators.containsKey(player)){
			creators.remove(player);
			return;
		}

		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(config.getNode("Buttons", locationName, "World").getString() == null){
				continue;
			}
			
			if(!player.hasPermission("pjp.button.break")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to break button portals"));
				event.setCancelled(true);
			}else{
				config.getNode("Buttons", locationName).setValue(null);
				configManager.save();
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke button portal"));
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
			
			if(!(type.equals(BlockTypes.STONE_BUTTON) && type.equals(BlockTypes.STONE_BUTTON))){
				continue;
			}

			Location<World> location = transaction.getFinal().getLocation().get();

			if(!player.hasPermission("pjp.button.place")){
	        	player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place button portals"));
	        	creators.remove(player);
	        	event.setCancelled(true);
	        	return;
			}

			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

            ConfigManager configManager = new ConfigManager("portals.conf");
            ConfigurationNode config = configManager.getConfig();

            Button button = creators.get(player);
            
            config.getNode("Buttons", locationName, "World").setValue(button.getLocation().getExtent().getName());
            if(!button.isSpawn()){
                config.getNode("Buttons", locationName, "X").setValue(button.getLocation().getBlockX());
                config.getNode("Buttons", locationName, "Y").setValue(button.getLocation().getBlockY());
                config.getNode("Buttons", locationName, "Z").setValue(button.getLocation().getBlockZ());
            }

            configManager.save();
            
    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Resource.spawnParticles(location, 1.0, false);
    		}

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "New button portal created"));
            
            creators.remove(player);
		}
	}
}
