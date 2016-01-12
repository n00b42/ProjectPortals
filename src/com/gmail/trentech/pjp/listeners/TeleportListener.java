package com.gmail.trentech.pjp.listeners;

import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.commands.CMDBack;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

public class TeleportListener {

	@Listener
	public void onTeleportEvent(TeleportEvent event){
		Player player = event.getTarget();
		
		Location<World> src = event.getSource();
		Location<World> dest = event.getDestination();

		if(!player.hasPermission("pjp.worlds." + dest.getExtent().getName()) && !player.hasPermission("pjw.worlds." + dest.getExtent().getName())){
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			event.setCancelled(true);
			return;
		}

		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();
		
		Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(dest);

		if(!optionalLocation.isPresent()){
			player.sendMessage(Text.builder().color(TextColors.DARK_RED).append(Text.of("Unsafe spawn point detected. Teleport anyway? "))
					.onClick(TextActions.executeCallback(Resource.unsafeTeleport(dest))).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			event.setCancelled(true);
			return;
		}
		
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Resource.spawnParticles(src, 0.5, true);
			Resource.spawnParticles(src.getRelative(Direction.UP), 0.5, true);
			
			Resource.spawnParticles(dest, 1.0, false);
			Resource.spawnParticles(dest.getRelative(Direction.UP), 1.0, false);
		}

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, Resource.getPrettyName(dest.getExtent().getName())), Text.of(TextColors.AQUA, "x: ", dest.getBlockX(), ", y: ", dest.getBlockY(),", z: ", dest.getBlockZ())));

		if(player.hasPermission("pjp.cmd.back")){
			CMDBack.players.put(player, src);
		}
	}
	
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		Player player = (Player) event.getTargetEntity();

		Location<World> src = event.getFromTransform().getLocation();
		Location<World> dest = event.getToTransform().getLocation();
		
		if(event.getFromTransform().getExtent() != event.getToTransform().getExtent()){
			if(player.hasPermission("pjp.cmd.back")){
				CMDBack.players.put(player, src);
			}
			return;
		}

		int srcX = src.getBlockX();
		int srcY = src.getBlockY();
		int srcZ = src.getBlockZ();
		
		int destX = dest.getBlockX();
		int destY = dest.getBlockY();
		int destZ = dest.getBlockZ();
		
		int distX = srcX - destX;
		int distY = srcY - destY;
		int distZ = srcZ - destZ;
		
		double distance = Math.sqrt(distX * distX + distY * distY + distZ * distZ);
		
		if(distance > 5){
			if(player.hasPermission("pjp.cmd.back")){
				CMDBack.players.put(player, src);
			}
		}
	}

	public static Consumer<CommandSource> unsafeTeleport(Location<World> location){
		return (CommandSource src) -> {
			Player player = (Player)src;

			player.setLocation(location);
			player.sendTitle(Title.of(Text.of(TextColors.GOLD, Resource.getPrettyName(location.getExtent().getName())), Text.of(TextColors.DARK_PURPLE, "x: ", location.getBlockX(), ", y: ", location.getBlockY(),", z: ", location.getBlockZ())));
		};
	}
}
