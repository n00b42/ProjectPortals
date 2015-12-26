package com.gmail.trentech.pjp.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;

public class CMDPortal implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Texts.builder().color(TextColors.DARK_GREEN).append(Texts.of(TextColors.AQUA, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		if(src.hasPermission("pjp.cmd.portal.cube")) {
			list.add(Texts.builder().color(TextColors.GREEN).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/portal help Cube")).append(Texts.of(" /portal cube")).build());
		}
		if(src.hasPermission("pjp.cmd.portal.plate")) {
			list.add(Texts.builder().color(TextColors.GREEN).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/portal help Plate")).append(Texts.of(" /portal plate")).build());
		}
		if(src.hasPermission("pjp.cmd.portal.button")) {
			list.add(Texts.builder().color(TextColors.GREEN).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/portal help Button")).append(Texts.of(" /portal button")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
