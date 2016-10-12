package com.gmail.trentech.pjp.commands.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Teleport;

public class CMDHome implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
		}
		Player player = (Player) src;

		if (args.hasAny("name")) {
			String name = args.<String> getOne("name").get().toLowerCase();

			Map<String, Portal> list = new HashMap<>();

			Optional<Map<String, Portal>> optionalList = player.get(Keys.PORTALS);

			if (optionalList.isPresent()) {
				list = optionalList.get();
			}

			if (!list.containsKey(name)) {
				throw new CommandException(Text.of(TextColors.RED, name, " does not exist"));
			}
			Portal.Local local = (Portal.Local) list.get(name);

			Optional<Location<World>> optionalLocation = local.getLocation();

			if (!optionalLocation.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, name, " has invalid location"));
			}

			if (args.hasAny("player")) {
				if (!src.hasPermission("pjp.cmd.home.others")) {
					throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp others"));
				}

				player = args.<Player> getOne("player").get();
			}
			
			Teleport.teleport(player, local);

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
		if (src.hasPermission("pjp.cmd.home.rename")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("hrename"))).append(Text.of(" /home rename")).build());
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
