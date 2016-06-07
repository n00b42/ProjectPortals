package com.gmail.trentech.pjp.commands.warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.object.Warp;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.events.TeleportEvent.Local;
import com.gmail.trentech.pjp.events.TeleportEvent.Server;
import com.gmail.trentech.pjp.utils.Help;

import flavor.pie.spongee.Spongee;

public class CMDWarp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(args.hasAny("name")) {
			if(!(src instanceof Player)) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
				return CommandResult.empty();
			}
			Player player = (Player) src;
			
			String warpName = args.<String>getOne("name").get().toLowerCase();
			
			Optional<Warp> optionalWarp = Warp.get(warpName);
			
			if(!optionalWarp.isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " does not exist"));
				return CommandResult.empty();
			}
			Warp warp = optionalWarp.get();
			
			if(!player.hasPermission("pjp.warps." + warpName)) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp here"));
				return CommandResult.empty();
			}
			
			if(args.hasAny("player")) {
				String playerName = args.<String>getOne("player").get();
				
				if(!src.hasPermission("pjp.cmd.warp.others")) {
					player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp others"));
					return CommandResult.empty();
				}

				Optional<Player> optionalPlayer = Main.getGame().getServer().getPlayer(playerName);
				
				if(!optionalPlayer.isPresent()) {
					player.sendMessage(Text.of(TextColors.DARK_RED, playerName, " does not exist"));
					return CommandResult.empty();
				}
				
				player = optionalPlayer.get();
			}
			
			if(warp.isBungee()) {
				Server teleportEvent = new TeleportEvent.Server(player, "", warp.getServer(), warp.getPrice(), Cause.of(NamedCause.source(warp)));

				if(!Main.getGame().getEventManager().post(teleportEvent)) {					
					Spongee.API.connectPlayer(player, teleportEvent.getDestination());
					
					player.setLocation(player.getWorld().getSpawnLocation());
				}
			}else {
				Optional<Location<World>> optionalSpawnLocation = warp.getDestination();
				
				if(!optionalSpawnLocation.isPresent()) {
					player.sendMessage(Text.of(TextColors.DARK_RED, "Destination does not exist or world is not loaded"));
					return CommandResult.empty();
				}			
				Location<World> spawnLocation = optionalSpawnLocation.get();

				Local teleportEvent = new TeleportEvent.Local(player, player.getLocation(), spawnLocation, warp.getPrice(), Cause.of(NamedCause.source("warp")));

				if(!Main.getGame().getEventManager().post(teleportEvent)) {
					spawnLocation = teleportEvent.getDestination();
					
					Vector3d rotation = warp.getRotation().toVector3d();

					player.setLocationAndRotation(spawnLocation, rotation);
				}
			}

			return CommandResult.success();
		}

		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		if(src.hasPermission("pjp.cmd.warp.others")) {
			list.add(Text.of(TextColors.YELLOW, " /warp <name> [player]\n"));
		}
		if(src.hasPermission("pjp.cmd.warp.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("wcreate"))).append(Text.of(" /warp create")).build());
		}
		if(src.hasPermission("pjp.cmd.warp.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("wremove"))).append(Text.of(" /warp remove")).build());
		}
		if(src.hasPermission("pjp.cmd.warp.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("wlist"))).append(Text.of(" /warp list")).build());
		}
		if(src.hasPermission("pjp.cmd.warp.price")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("wprice"))).append(Text.of(" /warp price")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
