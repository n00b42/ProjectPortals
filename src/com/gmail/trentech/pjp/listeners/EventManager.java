package com.gmail.trentech.pjp.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Resource;
import com.gmail.trentech.pjp.commands.CMDTeleportUnSafe;
import com.gmail.trentech.pjp.events.TeleportEvent;

public class EventManager {

	@Listener
	public void onTeleportEvent(TeleportEvent event, @First Player player){
		Location<World> src = event.getSrc();
		Location<World> dest = event.getDest();

		if(!player.hasPermission("pjw.worlds." + dest.getExtent().getName())){
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			return;
		}
		if(!player.setLocationSafely(dest)){
			CMDTeleportUnSafe.players.put(player, dest);
			player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/tu")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Resource.spawnParticles(src, 0.5, true);
			Resource.spawnParticles(src.getRelative(Direction.UP), 0.5, true);
			
			Resource.spawnParticles(dest, 1.0, false);
			Resource.spawnParticles(dest.getRelative(Direction.UP), 1.0, false);
		}

		player.sendTitle(Titles.of(Texts.of(TextColors.DARK_GREEN, dest.getExtent().getName()), Texts.of(TextColors.AQUA, "x: ", dest.getExtent().getSpawnLocation().getBlockX(), ", y: ", dest.getExtent().getSpawnLocation().getBlockY(),", z: ", dest.getExtent().getSpawnLocation().getBlockZ())));
	}
}
