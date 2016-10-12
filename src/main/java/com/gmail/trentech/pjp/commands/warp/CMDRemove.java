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

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("wremove", "remove", " Remove an existing  warp point");
		help.setPermission("pjp.cmd.warp.remove");
		help.setSyntax(" /warp remove <name>\n /w r <name>");
		help.setExample(" /warp remove OldSpawn");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String> getOne("name").get().toLowerCase();

		Optional<Portal> optionalPortal = Portal.get(name, PortalType.WARP);
		
		if (!optionalPortal.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}
		Portal portal = optionalPortal.get();
		
		portal.remove();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", name, " removed"));

		return CommandResult.success();
	}
}
