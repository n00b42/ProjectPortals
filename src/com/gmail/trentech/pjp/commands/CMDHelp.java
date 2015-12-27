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

public class CMDHelp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("command")) {
			Text t1 = Texts.of(TextColors.YELLOW, "/portal help ");
			Text t2 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Texts.of("Enter the command you need help with"))).append(Texts.of("<command> ")).build();
			src.sendMessage(Texts.of(t1,t2));
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
						+ " /portal button MyWorld -100 65 254";
				break;
			case "plate":
				description = " Use this command to create a pressure plate that will teleport you to other worlds";
				syntax = " /portal plate <world> [x] [y] [z]\n"
						+ " /p p <world> [x] [y] [z]";
				example = " /portal plate MyWorld\n"
						+ " /portal plate MyWorld -100 65 254";
				break;
			case "cube":
				description = " Create cuboid portal to another dimension, or specified location. No arguments allow for deleting portals.";
				syntax = " /portal cube <world> [x] [y] [z]\n"
						+ " /p c <world> [x] [y] [z]";;
				example = " /portal cube MyWorld\n"
						+ " /portal cube MyWorld -100 65 254\n"
						+ " /portal cube show";
				break;
			default:
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Not a valid command"));
				return CommandResult.empty();
		}
		
		help(command, description, syntax, example).sendTo(src);
		return CommandResult.success();
	}
	
	private PaginationBuilder help(String command, String description, String syntax, String example){
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

		pages.title(Texts.builder().color(TextColors.DARK_GREEN).append(Texts.of(TextColors.AQUA, command)).build());
		
		List<Text> list = new ArrayList<>();

		list.add(Texts.of(TextColors.AQUA, "Description:"));
		list.add(Texts.of(TextColors.GREEN, description));
		list.add(Texts.of(TextColors.AQUA, "Syntax:"));
		list.add(Texts.of(TextColors.GREEN, syntax));
		list.add(Texts.of(TextColors.AQUA, "Example:"));
		list.add(Texts.of(TextColors.GREEN,  example, TextColors.DARK_GREEN));
		
		pages.contents(list);
		
		return pages;
	}
}
