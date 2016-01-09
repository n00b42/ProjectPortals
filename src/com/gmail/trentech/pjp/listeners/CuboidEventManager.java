package com.gmail.trentech.pjp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.CuboidConstructEvent;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Cuboid;
import com.gmail.trentech.pjp.portals.CuboidBuilder;

import ninja.leaping.configurate.ConfigurationNode;

public class CuboidEventManager {

	@Listener
	public void onCuboidConstructEvent(CuboidConstructEvent event, @First Player player){
        if(!event.getLocations().isPresent()){
        	player.sendMessage(Text.of(TextColors.DARK_RED, "Cuboids cannot over lap over Cuboids"));
        	event.setCancelled(true);
        	return;
        }
        List<String> locations = event.getLocations().get();
        
        ConfigurationNode config = new ConfigManager().getConfig();
        
        int size = config.getNode("Options", "Cube", "Size").getInt();
        if(locations.size() > size){
        	player.sendMessage(Text.of(TextColors.DARK_RED, "Cuboids cannot be larger than ", size, " blocks"));
        	event.setCancelled(true);
        	return;
        }
        
        CuboidBuilder.getActiveBuilders().remove(player);
        CuboidBuilder.getCreators().add(player);
        
        player.sendMessage(Text.of(TextColors.DARK_GREEN, "New cube portal created"));
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Post event){
		ConfigManager configManager = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			if(!transaction.getOriginal().getState().getType().equals(BlockTypes.FLOWING_WATER)){
				continue;
			}
			
			List<Location<World>> list = new ArrayList<>();
			
			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.NORTH));
			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.SOUTH));
			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.EAST));
			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.WEST));
			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.UP));

			for(Location<World> location : list){
				String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

				if(configManager.getCuboid(locationName) == null){
					continue;
				}
				event.setCancelled(true);
			}
		}
	}
			
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @First Player player) {
		if(CuboidBuilder.getCreators().contains(player)){
			CuboidBuilder.getCreators().remove(player);
			return;
		}

		ConfigManager configManager = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(configManager.getCuboid(locationName) == null){
				continue;
			}
			
			if(!player.hasPermission("pjp.cube.place")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place blocks in cube portals"));
				event.setCancelled(true);
			}
		}
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		if(CuboidBuilder.getCreators().contains(player)){
			CuboidBuilder.getCreators().remove(player);
			return;
		}

		ConfigManager configManager = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(configManager.getCuboid(locationName) == null){
				continue;
			}
			
			if(!player.hasPermission("pjp.cube.break")){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to break blocks in cube portals"));
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event){
		if (!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();
		
		ConfigManager configManager = new ConfigManager("portals.conf");

		Location<World> location = player.getLocation();		
		String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		if(configManager.getCuboid(locationName) == null){
			return;
		}		
		Location<World> destination = configManager.getCuboid(locationName);
		
		if(!player.hasPermission("pjp.cube.interact")){
			player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with cube portals"));
			return;
		}
		
		Main.getGame().getEventManager().post(new TeleportEvent(player.getLocation(), destination, Cause.of(player)));
	}

	@Listener
	public void onInteractBlockEvent(InteractBlockEvent.Secondary event, @First Player player) {
		if(CuboidBuilder.getActiveBuilders().get(player) == null){
			return;
		}
		
		CuboidBuilder builder = CuboidBuilder.getActiveBuilders().get(player);
		
        ConfigManager loaderCuboids = new ConfigManager("portals.conf");

		if(!builder.getLocationType().isPresent()){
        	Location<World> location = event.getTargetBlock().getLocation().get();
        	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
        	
        	if(loaderCuboids.removeCuboidLocation(locationName)){
				CuboidBuilder.getActiveBuilders().remove(player);
				
                player.sendMessage(Text.of(TextColors.DARK_GREEN, "Cuboid has been removed"));
        	}
        	event.setCancelled(true);
		}else if(!builder.getLocation().isPresent()){
			builder.setLocation(event.getTargetBlock().getLocation().get());
			
			CuboidBuilder.getActiveBuilders().put(player, builder);
			
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Starting point selected"));
			event.setCancelled(true);
		}else{
			Cuboid Cuboid = new Cuboid(event.getTargetBlock().getState(), builder.getLocationType().get(), builder.getWorld(), builder.getDestination(), builder.getLocation().get(), event.getTargetBlock().getLocation().get());

			boolean CuboidConstructEvent = Main.getGame().getEventManager().post(new CuboidConstructEvent(Cuboid.getLocations(), Cause.of(player)));
			if(!CuboidConstructEvent) {
				Cuboid.build();
			}
			event.setCancelled(true);
		}
	}
}
