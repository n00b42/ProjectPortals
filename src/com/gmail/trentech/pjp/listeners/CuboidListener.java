package com.gmail.trentech.pjp.listeners;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.ConstructCuboidEvent;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Cuboid;
import com.gmail.trentech.pjp.portals.builders.Builder;
import com.gmail.trentech.pjp.portals.builders.CuboidBuilder;
import com.gmail.trentech.pjp.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CuboidListener {

	//private static List<Player> creators = new ArrayList<>();
	private static HashMap<Player, Builder> builders = new HashMap<>();
	
	@Listener
	public void onConstructCuboidEvent(ConstructCuboidEvent event, @First Player player){
		for(String locationName : event.getLocations()){
			if(Cuboid.get(locationName).isPresent()){
	        	player.sendMessage(Text.of(TextColors.DARK_RED, "Cuboids cannot over lap over Cuboids"));
	        	event.setCancelled(true);
	        	return;
			}
		}

        List<String> locations = event.getLocations();
        
        ConfigurationNode config = new ConfigManager().getConfig();
        
        int size = config.getNode("Options", "Cube", "Size").getInt();
        if(locations.size() > size){
        	player.sendMessage(Text.of(TextColors.DARK_RED, "Cuboids cannot be larger than ", size, " blocks"));
        	event.setCancelled(true);
        	return;
        }
        
        if(locations.size() == 1){
        	player.sendMessage(Text.of(TextColors.DARK_RED, "Cuboid too small"));
        	player.setItemInHand(null);
        	event.setCancelled(true);        	
        	return;
        }

        //creators.add(player);
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @First Player player) {
//		if(creators.contains(player)){
//			creators.remove(player);
//			return;
//		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		

			if(!Cuboid.listAllLocations().contains(location)){
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
//		if(creators.contains(player)){
//			creators.remove(player);
//			return;
//		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		

			if(!Cuboid.listAllLocations().contains(location)){
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

		Location<World> location = player.getLocation();		
		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		if(!Cuboid.get(locationName).isPresent()){
			return;
		}
		Cuboid cuboid = Cuboid.get(locationName).get();

		if(!player.hasPermission("pjp.cube.interact")){
			player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with button portals"));
			event.setCancelled(true);
			return;
		}
		
		if(!cuboid.getDestination().isPresent()){
			player.sendMessage(Text.of(TextColors.DARK_RED, "World does not exist"));
			return;
		}
		Location<World> spawnLocation = cuboid.getDestination().get();

		TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of("cube"));

		if(!Main.getGame().getEventManager().post(teleportEvent)){
			spawnLocation = teleportEvent.getDestination();
			player.setLocation(spawnLocation);
		}
	}

	@Listener
	public void onInteractBlockEvent(InteractBlockEvent.Secondary event, @First Player player) {
		if(!builders.containsKey(player)){
			return;
		}
		CuboidBuilder builder = (CuboidBuilder) builders.get(player);

		if(!builder.getStartLocation().isPresent()){
			builder.start(event.getTargetBlock().getLocation().get());
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Starting point selected"));
		}else{
			builder.end(event.getTargetBlock().getLocation().get());
			
			if(builder.build(event.getTargetBlock().getState())){
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Cube portal ", builder.getName(), " created successfully"));
			}
			builders.remove(player);
		}
		event.setCancelled(true);
	}
	
//    @Listener
//    public void onDamageEntityEvent(DamageEntityEvent event, @First BlockDamageSource damageSource) {
//    	if(!(event.getTargetEntity() instanceof Player)) {
//    		return;
//    	}
//
//        BlockSnapshot block = damageSource.getBlockSnapshot();
//        
//        if(!block.getState().getType().equals(BlockTypes.FLOWING_LAVA)){
//        	return;
//        }
//        
//        Location<World> location = block.getLocation().get();
//        
//		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
//
//		if(Cuboid.get(locationName).isPresent()){
//			return;
//		}
//		
//		event.setCancelled(true);
//    }
    
//    @Listener
//    public void onIgniteEntityEvent(IgniteEntityEvent event, @First BlockDamageSource damageSource) {
//    	if(!(event.getTargetEntity() instanceof Player)) {
//    		return;
//    	}
//
//        BlockSnapshot block = damageSource.getBlockSnapshot();
//        
//        if(!block.getState().getType().equals(BlockTypes.FLOWING_LAVA)){
//        	return;
//        }
//        
//        Location<World> location = block.getLocation().get();
//        
//		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
//
//		if(Cuboid.get(locationName).isPresent()){
//			return;
//		}
//		
//		event.setCancelled(true);
//    }
	
//	@Listener
//	public void onChangeBlockEvent(ChangeBlockEvent.Post event){
//		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
//			if(!transaction.getOriginal().getState().getType().equals(BlockTypes.FLOWING_WATER) && !transaction.getOriginal().getState().getType().equals(BlockTypes.WATER)){
//				continue;
//			}
//
//			List<Location<World>> list = new ArrayList<>();
//			
//			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.NORTH));
//			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.SOUTH));
//			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.EAST));
//			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.WEST));
//			list.add(transaction.getOriginal().getLocation().get().getRelative(Direction.UP));
//
//			for(Location<World> location : list){
//				String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
//
//				if(!Cuboid.get(locationName).isPresent()){
//					continue;
//				}
//				
//				event.setCancelled(true);
//			}
//		}
//	}
	public static HashMap<Player, Builder> getBuilders() {
		return builders;
	}
}
