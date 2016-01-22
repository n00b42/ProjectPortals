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
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.portals.Warp;
import com.gmail.trentech.pjp.utils.Help;

public class CMDWarp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(args.hasAny("name")) {
			String warpName = args.<String>getOne("name").get();
			
			Optional<Warp> optionalWarp = Warp.get(warpName);
			
			if(!optionalWarp.isPresent()){
				src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " does not exist"));
				return CommandResult.empty();
			}
			Warp warp = optionalWarp.get();
			
			if(!player.hasPermission("pjp.warps." + warpName)){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp here"));
				return CommandResult.empty();
			}
			
			Optional<Location<World>> optionalSpawnLocation = warp.getDestination();
			
			if(!optionalSpawnLocation.isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, warp.destination.split(":")[0], " does not exist"));
				return CommandResult.empty();
			}			
			Location<World> spawnLocation = optionalSpawnLocation.get();
			
			if(args.hasAny("player")) {
				String playerName = args.<String>getOne("player").get();
				
				if(!src.hasPermission("pjp.cmd.warp.others")) {
					player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp others"));
					return CommandResult.empty();
				}

				Optional<Player> optionalPlayer = Main.getGame().getServer().getPlayer(playerName);
				
				if(!optionalPlayer.isPresent()){
					player.sendMessage(Text.of(TextColors.DARK_RED, playerName, " does not exist"));
					return CommandResult.empty();
				}
				
				player = optionalPlayer.get();
			}

			TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, Cause.of("warp"));

			if(!Main.getGame().getEventManager().post(teleportEvent)){
				spawnLocation = teleportEvent.getDestination();
				player.setLocation(spawnLocation);
			}

			return CommandResult.success();
		}

		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
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
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
