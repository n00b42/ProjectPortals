package com.gmail.trentech.pjp.commands.portal;

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
		new Help("portal remove", "remove", "Remove an existing portal", false)
			.setPermission("pjp.cmd.portal.remove")
			.setUsage("/portal remove <name>\n /p r <name>")
			.setExample("/portal remove MyPortal")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Portal portal = args.<Portal>getOne("name").get();

		portal.remove();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal ", portal.getName(), " removed"));

		return CommandResult.success();
	}
}
