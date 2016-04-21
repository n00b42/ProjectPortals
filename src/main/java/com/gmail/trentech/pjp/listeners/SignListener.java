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
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent.TargetPlayer;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.immutable.ImmutablePortalData;
import com.gmail.trentech.pjp.data.mutable.PortalData;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.utils.ConfigManager;

public class SignListener {
	
	public static ConcurrentHashMap<Player, PortalData> builders = new ConcurrentHashMap<>();
	
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

		event.getTargetTile().offer(portalData);

		String[] split = new ConfigManager().getConfig().getNode("options", "particles", "type", "creation").getString().split(":");
		
		Optional<Particle> optionalParticle = Particles.get(split[0]);
		
		if(optionalParticle.isPresent()){
			Particle particle = optionalParticle.get();
			
			if(split.length == 2 && particle.isColorable()){
				Optional<ParticleColor> optionalColors = ParticleColor.get(split[1]);
				
				if(optionalColors.isPresent()){
					particle.spawnParticle(event.getTargetTile().getLocation(), optionalColors.get());
				}else{
					particle.spawnParticle(event.getTargetTile().getLocation());
				}
			}else{
				particle.spawnParticle(event.getTargetTile().getLocation());
			}
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
			return;
		}
		Location<World> spawnLocation = optionalSpawnLocation.get();
		
		if(new ConfigManager().getConfig().getNode("options", "advanced_permissions").getBoolean()){
			if(!player.hasPermission("pjp.sign." + location.getExtent().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ())){
				player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to use this sign portal"));
				event.setCancelled(true);
				return;
			}
		}else{
			if(!player.hasPermission("pjp.sign.interact")) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to interact with sign portals"));
				event.setCancelled(true);
				return;
			}
		}

		TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, 0, Cause.of(NamedCause.source(portalData)));

		if(!Main.getGame().getEventManager().post(teleportEvent)){
			Location<World> currentLocation = player.getLocation();
			spawnLocation = teleportEvent.getDestination();
			
			Optional<Vector3d> optionalRotation = portalData.getRotation();
			
			if(optionalRotation.isPresent()){
				player.setLocationAndRotation(spawnLocation, optionalRotation.get());
			}else{
				player.setLocation(spawnLocation);
			}
			
			TargetPlayer displaceEvent = SpongeEventFactory.createDisplaceEntityEventTargetPlayer(Cause.of(NamedCause.source(this)), new Transform<World>(currentLocation), new Transform<World>(spawnLocation), player);
			Main.getGame().getEventManager().post(displaceEvent);
		}
	}
	
	@Listener
	public void onSignBreakEvent(ChangeBlockEvent.Break event, @First Player player) {
	    for(Transaction<BlockSnapshot> blockTransaction : event.getTransactions()){
    		BlockSnapshot snapshot = blockTransaction.getOriginal();

    		BlockType blockType = snapshot.getState().getType();

    		if(!blockType.equals(BlockTypes.WALL_SIGN) && !blockType.equals(BlockTypes.STANDING_SIGN)){
    			continue;
    		}

    		Optional<ImmutablePortalData> optionalPortalData = snapshot.get(ImmutablePortalData.class);
    		
			if(!optionalPortalData.isPresent()){
				continue;
			}

			if(!player.hasPermission("pjp.sign.break")) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to break sign portal"));
				event.setCancelled(true);
			}else{
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke sign portal"));
			}
			return;
	    }
	}
}
