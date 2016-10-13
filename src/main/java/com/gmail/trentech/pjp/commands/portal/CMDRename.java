package com.gmail.trentech.pjp.commands.portal;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		Help help = new Help("portal rename", "rename", " Rename portal", false);
		help.setPermission("pjp.cmd.portal.rename");
		help.setSyntax(" /portal rename <oldName> <newName>\n /p rn <oldName> <newName>");
		help.setExample(" /portal rename MyPortal ThisPortal");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String oldName = args.<String>getOne("oldName").get().toLowerCase();

		Optional<Portal> optionalPortal = Portal.get(oldName, PortalType.PORTAL);

		if (!optionalPortal.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, oldName, " does not exist"), false);
		}
		Portal portal = optionalPortal.get();

		String newName = args.<String>getOne("newName").get().toLowerCase();

		if (Portal.get(newName, PortalType.PORTAL).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
		}

		portal.remove();
		portal.create(newName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal renamed to ", newName));

		return CommandResult.success();
	}

}
