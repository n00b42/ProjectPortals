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

public class CMDPrice implements CommandExecutor {

	public CMDPrice() {
		Help help = new Help("wprice", "price", " Charge players for using warps. 0 to disable");
		help.setPermission("pjp.cmd.warp.price");
		help.setSyntax(" /warp price <name> <price>\n /w p <name> <price>");
		help.setExample(" /warp price Lobby 50\n /warp price Lobby 0");
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

		double price = args.<Double> getOne("price").get();

		portal.setPrice(price);
		portal.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set price of warp ", name, " to $", price));

		return CommandResult.success();
	}
}
