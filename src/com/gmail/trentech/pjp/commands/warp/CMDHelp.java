package com.gmail.trentech.pjp.commands.warp;

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

public class CMDHelp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("command")) {
			Text t1 = Text.of(TextColors.YELLOW, "/portal help ");
			Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the command you need help with"))).append(Text.of("<command> ")).build();
			src.sendMessage(Text.of(t1,t2));
			return CommandResult.empty();
		}
		String command = args.<String>getOne("command").get();
		String description = null;
		String syntax = null;
		String example = null;
		
		switch(command.toLowerCase()){
			case "create":
				description = " Create a new warp point";
				syntax = " /warp create <name>\n"
						+ " /wp c <name>";
				example = " /warp create Spawn";
				break;
			case "remove":
				description = " Remove an existing warp point";
				syntax = " /warp remove <name>"
						+ " /wp r <name>";
				example = " /warp remove Spawn";
				break;
			case "list":
				description = " List all available warps.";
				syntax = " /warp list"
						+ " /wp l";
				example = " /warp list";
				break;
			default:
				src.sendMessage(Text.of(TextColors.DARK_RED, "Not a valid command"));
				return CommandResult.empty();
		}
		
		help(command, description, syntax, example).sendTo(src);
		return CommandResult.success();
	}
	
	private PaginationBuilder help(String command, String description, String syntax, String example){
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, command)).build());
		
		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.AQUA, "Description:"));
		list.add(Text.of(TextColors.GREEN, description));
		list.add(Text.of(TextColors.AQUA, "Syntax:"));
		list.add(Text.of(TextColors.GREEN, syntax));
		list.add(Text.of(TextColors.AQUA, "Example:"));
		list.add(Text.of(TextColors.GREEN,  example, TextColors.DARK_GREEN));
		
		pages.contents(list);
		
		return pages;
	}
}
