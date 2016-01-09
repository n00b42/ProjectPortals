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
			case "button":
				description = " Use this command to create a button that will teleport you to other worlds";
				syntax = " /portal button <world> [x] [y] [z]\n"
						+ " /p b <world> [x] [y] [z]";
				example = " /portal button MyWorld\n"
						+ " /portal button MyWorld -100 65 254\n"
						+ " /portal button MyWorld random";
				break;
			case "plate":
				description = " Use this command to create a pressure plate that will teleport you to other worlds";
				syntax = " /portal plate <world> [x] [y] [z]\n"
						+ " /p p <world> [x] [y] [z]";
				example = " /portal plate MyWorld\n"
						+ " /portal plate MyWorld -100 65 254\n"
						+ " /portal plate MyWorld random";
				break;
			case "cube":
				description = " Create cuboid portal to another dimension, or specified location. No arguments allow for deleting portals.";
				syntax = " /portal cube <world> [x] [y] [z]\n"
						+ " /p c <world> [x] [y] [z]";;
				example = " /portal cube MyWorld\n"
						+ " /portal cube MyWorld -100 65 254\n"
						+ " /portal cube MyWorld random\n"
						+ " /portal cube show"
						+ " /portal cube remove";
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
