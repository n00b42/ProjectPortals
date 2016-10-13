package com.gmail.trentech.pjp.commands.warp;

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
		Help help = new Help("warp rename", "rename", " Rename warp", false);
		help.setPermission("pjp.cmd.warp.rename");
		help.setSyntax(" /warp rename <oldName> <newName>\n /w rn <oldName> <newName>");
		help.setExample(" /warp rename Spawn Lobby");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String oldName = args.<String>getOne("oldName").get().toLowerCase();

		Optional<Portal> optionalPortal = Portal.get(oldName, PortalType.WARP);

		if (!optionalPortal.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, oldName, " does not exist"), false);
		}
		Portal portal = optionalPortal.get();

		String newName = args.<String>getOne("newName").get().toLowerCase();

		if (Portal.get(newName, PortalType.WARP).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
		}

		portal.remove();
		portal.create(newName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp renamed to ", newName));

		return CommandResult.success();
	}

}
