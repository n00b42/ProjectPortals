package com.gmail.trentech.pjp.commands.portal;

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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.Help;

public class CMDPortal implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());
		
		List<Text> list = new ArrayList<>();

		if(src.hasPermission("pjp.cmd.portal.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("portal command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("pcreate"))).append(Text.of(" /portal create")).build());
		}
		if(src.hasPermission("pjp.cmd.portal.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("premove"))).append(Text.of(" /portal remove")).build());
		}
		if(src.hasPermission("pjp.cmd.portal.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("plist"))).append(Text.of(" /portal list")).build());
		}
		if(src.hasPermission("pjp.cmd.portal.save")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("save"))).append(Text.of(" /portal save")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
