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

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("portal remove", "remove", " Remove an existing portal", false);
		help.setPermission("pjp.cmd.portal.remove");
		help.setSyntax(" /portal remove <name>\n /p r <name>");
		help.setExample(" /portal remove MyPortal");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String>getOne("name").get().toLowerCase();

		Optional<Portal> optionalPortal = Portal.get(name, PortalType.PORTAL);

		if (!optionalPortal.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}
		Portal portal = optionalPortal.get();

		portal.remove();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal ", name, " removed"));

		return CommandResult.success();
	}
}
