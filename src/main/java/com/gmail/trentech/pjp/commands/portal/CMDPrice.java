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

public class CMDPrice implements CommandExecutor {

	public CMDPrice() {
		Help help = new Help("pprice", "price", " Charge players for using portals. 0 to disable");
		help.setPermission("pjp.cmd.portal.price");
		help.setSyntax(" /portal price <name> <price>\n /p pr <name> <price>");
		help.setExample(" /portal price MyPortal 50\n /portal price MyPortal 0");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String> getOne("name").get().toLowerCase();

		Optional<Portal> optionalPortal = Portal.get(name, PortalType.PORTAL);
		
		if (!optionalPortal.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}
		Portal portal = optionalPortal.get();

		double price = args.<Double> getOne("price").get();

		portal.setPrice(price);
		portal.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set price of portal ", name, " to $", price));

		return CommandResult.success();
	}
}
