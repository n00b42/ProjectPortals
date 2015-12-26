package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.Resource;
import com.gmail.trentech.pjp.events.TeleportEvent;

import ninja.leaping.configurate.ConfigurationNode;

public class PlateEventManager {

	public static HashMap<Player, Location<World>> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot block = transaction.getFinal();

			if(!(block.getState().getType().getName().toUpperCase().contains("_PRESSURE_PLATE"))){
				return;
			}

			if(!block.get(Keys.POWERED).isPresent()){
				return;
			}

			if(!block.get(Keys.POWERED).get()){
				return;
			}
			
			Location<World> location = block.getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			ConfigurationNode config = new ConfigManager("portals.conf").getConfig();;
			
			if(config.getNode("Plates", locationName, "World").getString() == null){
				return;
			}
			
			String worldName = config.getNode("Plates", locationName, "World").getString();
			int x = config.getNode("Plates", locationName, "X").getInt();
			int y = config.getNode("Plates", locationName, "Y").getInt();
			int z = config.getNode("Plates", locationName, "Z").getInt();
			
			if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
				player.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " does not exist"));
				return;
			}
			World world = Main.getGame().getServer().getWorld(worldName).get();
			
			if(!player.hasPermission("pjp.plate.interact." + worldName)){
				player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
				return;
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

		ConfigManager loader = new ConfigManager("portals.conf");
		ConfigurationNode config = loader.getConfig();
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(config.getNode("Plates", locationName, "World").getString() == null){
				continue;
			}
			
			if(!player.hasPermission("pjp.plate.break")){
				player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
				event.setCancelled(true);
			}else{
				config.getNode("Plates", locationName).setValue(null);
				loader.save();
				player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Broke pressure plate portal"));
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @First Player player) {
		if(!creators.containsKey(player)){
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			if(!(transaction.getFinal().getState().getType().getName().toUpperCase().contains("_PRESSURE_PLATE"))){		
				continue;
			}
			
			Location<World> location = transaction.getFinal().getLocation().get();
			
			if(!player.hasPermission("pjp.button.place." + location.getExtent().getName())){
	        	player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to create teleport pressure playes in this world"));
	        	return;
			}
			
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

            ConfigManager loader = new ConfigManager("portals.conf");
            ConfigurationNode config = loader.getConfig();

            Location<World> destination = creators.get(player);
            
            config.getNode("Plates", locationName, "World").setValue(destination.getExtent().getName());
            config.getNode("Plates", locationName, "X").setValue(destination.getBlockX());
            config.getNode("Plates", locationName, "Y").setValue(destination.getBlockY());
            config.getNode("Plates", locationName, "Z").setValue(destination.getBlockZ());

            loader.save();
          
    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Resource.spawnParticles(location, 1.0, false);
    		}
            
            player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New pressure plate portal created"));
            
            creators.remove(player);
		}
	}
}
