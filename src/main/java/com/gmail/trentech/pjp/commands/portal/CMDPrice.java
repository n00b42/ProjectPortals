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

public class CMDPrice implements CommandExecutor {

	public CMDPrice() {
		Help help = new Help("portal price", "price", " Charge players for using portals. 0 to disable", false);
		help.setPermission("pjp.cmd.portal.price");
		help.setSyntax(" /portal price <name> <price>\n /p pr <name> <price>");
		help.setExample(" /portal price MyPortal 50\n /portal price MyPortal 0");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Portal portal = args.<Portal>getOne("name").get();

		double price = args.<Double>getOne("price").get();

		portal.setPrice(price);
		portal.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set price of portal ", portal.getName(), " to $", price));

		return CommandResult.success();
	}
}
