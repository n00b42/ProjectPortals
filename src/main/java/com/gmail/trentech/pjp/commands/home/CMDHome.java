package com.gmail.trentech.pjp.commands.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.object.Home;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.events.TeleportEvent.Local;
import com.gmail.trentech.pjp.utils.Help;

public class CMDHome implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
		}
		Player player = (Player) src;

		if (args.hasAny("name")) {
			String homeName = args.<String> getOne("name").get().toLowerCase();

			Map<String, Home> homeList = new HashMap<>();

			Optional<Map<String, Home>> optionalHomeList = player.get(Keys.HOMES);

			if (optionalHomeList.isPresent()) {
				homeList = optionalHomeList.get();
			} else {
				player.offer(new HomeData(new HashMap<String, Home>()));
			}

			if (!homeList.containsKey(homeName)) {
				throw new CommandException(Text.of(TextColors.RED, homeName, " does not exist"));
			}
			Home home = homeList.get(homeName);

			Optional<Location<World>> optionalSpawnLocation = home.getDestination();

			if (!optionalSpawnLocation.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, homeName, " is invalid"));
			}
			Location<World> spawnLocation = optionalSpawnLocation.get();

			if (args.hasAny("player")) {
				if (!src.hasPermission("pjp.cmd.home.others")) {
					throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp others"));
				}

				player = args.<Player> getOne("player").get();
			}

			Local teleportEvent = new TeleportEvent.Local(player, player.getLocation(), spawnLocation, 0, Cause.of(NamedCause.source("home")));

			if (!Sponge.getEventManager().post(teleportEvent)) {
				spawnLocation = teleportEvent.getDestination();

				player.setLocationAndRotation(spawnLocation, home.getRotation().toVector3d());
			}

			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pjp.cmd.home.others")) {
			list.add(Text.of(TextColors.YELLOW, " /home <name> [player]\n"));
		}
		if (src.hasPermission("pjp.cmd.home.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("hcreate"))).append(Text.of(" /home create")).build());
		}
		if (src.hasPermission("pjp.cmd.home.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("hremove"))).append(Text.of(" /home remove")).build());
		}
		if (src.hasPermission("pjp.cmd.home.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("hlist"))).append(Text.of(" /home list")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}
