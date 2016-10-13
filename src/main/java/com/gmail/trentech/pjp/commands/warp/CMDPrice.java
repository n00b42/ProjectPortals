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

public class CMDPrice implements CommandExecutor {

	public CMDPrice() {
		Help help = new Help("warp price", "price", " Charge players for using warps. 0 to disable", false);
		help.setPermission("pjp.cmd.warp.price");
		help.setSyntax(" /warp price <name> <price>\n /w p <name> <price>");
		help.setExample(" /warp price Lobby 50\n /warp price Lobby 0");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Portal portal = args.<Portal>getOne("name").get();

		double price = args.<Double>getOne("price").get();

		portal.setPrice(price);
		portal.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set price of warp ", portal.getName(), " to $", price));

		return CommandResult.success();
	}
}
