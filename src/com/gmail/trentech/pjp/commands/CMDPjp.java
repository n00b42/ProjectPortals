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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDPjp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		ConfigurationNode node = new ConfigManager().getConfig().getNode("Options", "Modules");
		
		if(src.hasPermission("pjp.cmd.cube") && node.getNode("Cubes").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/pjp:cube")).append(Text.of(" /cube")).build());
		}
//		if(src.hasPermission("pjp.cmd.portals") && node.getNode("Portals").getBoolean()) {
//			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
//					.onClick(TextActions.runCommand("/pjp:portal")).append(Text.of(" /portal")).build());
//		}
		if(src.hasPermission("pjp.cmd.plate") && node.getNode("Plates").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("plate"))).append(Text.of(" /plate")).build());
		}
		if(src.hasPermission("pjp.cmd.lever") && node.getNode("Levers").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("lever"))).append(Text.of(" /lever")).build());
		}
		if(src.hasPermission("pjp.cmd.button") && node.getNode("Buttons").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("button"))).append(Text.of(" /button")).build());
		}
		if(src.hasPermission("pjp.cmd.home") && node.getNode("Homes").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/pjp:home")).append(Text.of(" /home")).build());
		}
		if(src.hasPermission("pjp.cmd.warp") && node.getNode("Warps").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/pjp:warp")).append(Text.of(" /warp")).build());
		}
		if(src.hasPermission("pjp.cmd.back")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("back"))).append(Text.of(" /back")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
