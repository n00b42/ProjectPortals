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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Teleport;

public class CMDWarp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (args.hasAny("name")) {
			if (!(src instanceof Player)) {
				throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
			}
			Player player = ((Player) src);

			String name = args.<String>getOne("name").get().toLowerCase();

			Optional<Portal> optionalPortal = Portal.get(name, PortalType.WARP);

			if (!optionalPortal.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
			}
			Portal portal = optionalPortal.get();

			if (!player.hasPermission("pjp.warps." + name)) {
				throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp here"));
			}

			if (args.hasAny("player")) {
				if (!src.hasPermission("pjp.cmd.warp.others")) {
					throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp others"));
				}

				player = args.<Player>getOne("player").get();
			}

			Teleport.teleport(player, portal);

			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pjp.cmd.warp.others")) {
			list.add(Text.of(TextColors.YELLOW, " /warp <name> [player]\n"));
		}
		if (src.hasPermission("pjp.cmd.warp.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("wcreate"))).append(Text.of(" /warp create")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("wremove"))).append(Text.of(" /warp remove")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.rename")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("wrename"))).append(Text.of(" /warp rename")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("wlist"))).append(Text.of(" /warp list")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.price")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("wprice"))).append(Text.of(" /warp price")).build());
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
