package com.gmail.trentech.pjp.commands.portal;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.data.portal.Portal;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		Help help = new Help("prename", "rename", " Rename portal");
		help.setPermission("pjp.cmd.portal.rename");
		help.setSyntax(" /portal rename <oldName> <newName>\n /p rn <oldName> <newName>");
		help.setExample(" /portal rename MyPortal ThisPortal");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String oldName = args.<String> getOne("oldName").get().toLowerCase();

		if (!Portal.get(oldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, oldName, " does not exist"), false);
		}
		Portal portal = Portal.get(oldName).get();

		String newName = args.<String> getOne("newName").get().toLowerCase();

		if (Portal.get(newName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
		}

		portal.remove();
		portal.create(newName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal renamed to ", newName));
		
		return CommandResult.success();
	}

}
