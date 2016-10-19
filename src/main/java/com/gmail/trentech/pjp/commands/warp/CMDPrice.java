package com.gmail.trentech.pjp.commands.warp;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portal.Portal;

public class CMDPrice implements CommandExecutor {

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
