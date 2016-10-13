package com.gmail.trentech.pjp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.base.CharMatcher;

public class Help {

	private final String rawCommand;
	private final String command;
	private final String description;
	private final boolean hasChildren;
	private Optional<String> permission = Optional.empty();
	private Optional<String> syntax = Optional.empty();
	private Optional<String> example = Optional.empty();

	private static TreeMap<String, Help> map = new TreeMap<>();

	public Help(String rawCommand, String command, String description, boolean hasChildren) {
		this.rawCommand = rawCommand;
		this.command = command;
		this.description = description;
		this.hasChildren = hasChildren;
	}

	public String getRawCommand() {
		return rawCommand;
	}

	public String getDescription() {
		return description;
	}

	public boolean hasChildren() {
		return hasChildren;
	}
	
	public Optional<String> getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = Optional.of(permission);
	}

	public Optional<String> getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = Optional.of(syntax);
	}

	public Optional<String> getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = Optional.of(example);
	}

	public String getCommand() {
		return command;
	}

	public boolean isParent() {
		return CharMatcher.WHITESPACE.matchesNoneOf(getRawCommand());
	}
	
	public void save() {
		map.put(getRawCommand(), this);
	}

	private void execute(CommandSource src) {
		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, "Description:"));
		list.add(Text.of(TextColors.WHITE, getDescription()));

		if (getPermission().isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Permission:"));
			list.add(Text.of(TextColors.WHITE, " ", getPermission().get()));
		}
		if (getSyntax().isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Syntax:"));
			list.add(Text.of(TextColors.WHITE, getSyntax().get()));
		}
		if (getExample().isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Example:"));
			list.add(Text.of(TextColors.WHITE, getExample().get(), TextColors.DARK_GREEN));
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, getCommand().toLowerCase())).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}
	}

	public static Optional<Help> get(String rawCommand) {
		if (map.containsKey(rawCommand)) {
			return Optional.of(map.get(rawCommand));
		}

		return Optional.empty();
	}

	public static Consumer<CommandSource> execute(String rawCommand) {
		return (CommandSource src) -> {
			if (map.containsKey(rawCommand)) {
				Help help = map.get(rawCommand);
				help.execute(src);
			}
		};
	}
	
	public static List<Help> getParents() {
		List<Help> list = new ArrayList<>();
		
		for(Entry<String, Help> entry : map.entrySet()) {
			Help help = entry.getValue();
			
			if(help.isParent()) {
				list.add(help);
			}
		}
		
		return list;
	}
	
	public static List<Help> getChildren(String parentCommand) {
		List<Help> list = new ArrayList<>();
		
		for(Entry<String, Help> entry : map.entrySet()) {
			Help help = entry.getValue();

			if(help.getRawCommand().startsWith(parentCommand) && !help.isParent()) {
				list.add(help);
			}
		}

		return list;
	}
	
	public static List<Text> getList(CommandSource src) {
		return getList(src, getParents());
	}
	
	public static List<Text> getList(CommandSource src, String parentCommand) {
		return getList(src, getChildren(parentCommand));
	}
	
	private static List<Text> getList(CommandSource src, List<Help> list) {
		List<Text> pages = new ArrayList<>();
		
		for (Help help : list) {

			Optional<String> optionalPermission = help.getPermission();
			
			if(optionalPermission.isPresent()) {
				if (src.hasPermission(optionalPermission.get())) {
					if(help.hasChildren()) {
						pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.runCommand("/" + help.getRawCommand())).append(Text.of("/" + help.getRawCommand())).build());
					} else {
						pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.execute(help.getRawCommand()))).append(Text.of("/" + help.getRawCommand())).build());
					}	
				}
			} else {
				if(help.hasChildren()) {
					pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.runCommand("/" + help.getRawCommand())).append(Text.of("/" + help.getRawCommand())).build());
				} else {
					pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.execute(help.getRawCommand()))).append(Text.of("/" + help.getRawCommand())).build());
				}
			}
		}
		
		return pages;
	}
}
