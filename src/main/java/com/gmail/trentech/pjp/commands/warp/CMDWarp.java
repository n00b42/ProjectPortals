package com.gmail.trentech.pjp.commands.warp;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Teleport;

public class CMDWarp implements CommandExecutor {

	public CMDWarp() {
		Help help = new Help("warp", "warp", " Top level warp command", true);
		help.setPermission("pjp.cmd.warp");
		help.save();
	}
	
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

		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.YELLOW, " /warp <name> [player]"));
		
		list.addAll(Help.getList(src, "warp"));

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
