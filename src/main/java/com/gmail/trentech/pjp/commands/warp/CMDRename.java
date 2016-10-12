package com.gmail.trentech.pjp.commands.warp;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.data.portal.Portal;
import com.gmail.trentech.pjp.data.portal.Warp;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		Help help = new Help("wrename", "rename", " Rename portal");
		help.setPermission("pjp.cmd.warp.rename");
		help.setSyntax(" /warp rename <oldName> <newName>\n /w rn <oldName> <newName>");
		help.setExample(" /warp rename MyPortal ThisPortal");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String oldName = args.<String> getOne("oldName").get().toLowerCase();

		if (!Warp.get(oldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, oldName, " does not exist"), false);
		}
		Warp warp = Warp.get(oldName).get();

		String newName = args.<String> getOne("newName").get().toLowerCase();

		if (Portal.get(newName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
		}

		warp.remove();
		warp.create(newName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp renamed to ", newName));
		
		return CommandResult.success();
	}

}
