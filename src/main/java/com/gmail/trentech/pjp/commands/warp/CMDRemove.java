package com.gmail.trentech.pjp.commands.warp;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("warp remove", "remove", " Remove an existing  warp point", false);
		help.setPermission("pjp.cmd.warp.remove");
		help.setSyntax(" /warp remove <name>\n /w r <name>");
		help.setExample(" /warp remove OldSpawn");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Portal portal = args.<Portal>getOne("name").get();

		portal.remove();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", portal.getName(), " removed"));

		return CommandResult.success();
	}
}
