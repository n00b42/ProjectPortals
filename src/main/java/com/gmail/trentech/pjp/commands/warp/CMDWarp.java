package com.gmail.trentech.pjp.commands.warp;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
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

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.utils.Teleport;

public class CMDWarp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (args.hasAny("name")) {
			if (!(src instanceof Player)) {
				throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
			}
			Player player = ((Player) src);

			Portal portal = args.<Portal>getOne("name").get();

			if (!player.hasPermission("pjp.warps." + portal.getName())) {
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

		src.sendMessage(Text.of(TextColors.YELLOW, " /warp <name> [player]"));
		
		if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
			Help.executeList(src, Help.get("warp").get().getChildren());
			
			return CommandResult.success();
		}
		
		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pjp.cmd.warp.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:warp create")).append(Text.of(" /warp create")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:warp remove")).append(Text.of(" /warp remove")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.rename")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:warp rename")).append(Text.of(" /warp rename")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:warp list")).append(Text.of(" /warp list")).build());
		}
		if (src.hasPermission("pjp.cmd.warp.price")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:warp price")).append(Text.of(" /warp price")).build());
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
