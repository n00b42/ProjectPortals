package com.gmail.trentech.pjp.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.pjp.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDPjp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		ConfigurationNode node = ConfigManager.get().getConfig().getNode("settings", "modules");
		
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			List<Help> commands = new ArrayList<>();
			
			if (node.getNode("portals").getBoolean()) {
				commands.add(Help.get("portal").get());
			}
			if (node.getNode("plates").getBoolean()) {
				commands.add(Help.get("plate").get());
			}
			if (node.getNode("levers").getBoolean()) {
				commands.add(Help.get("lever").get());
			}
			if (node.getNode("signs").getBoolean()) {
				commands.add(Help.get("sign").get());
			}
			if (node.getNode("doors").getBoolean()) {
				commands.add(Help.get("door").get());
			}
			if (node.getNode("buttons").getBoolean()) {
				commands.add(Help.get("button").get());
			}
			if (node.getNode("homes").getBoolean()) {
				commands.add(Help.get("home").get());
			}
			if (node.getNode("warps").getBoolean()) {
				commands.add(Help.get("warp").get());
			}
			if (src.hasPermission("pjp.cmd.back")) {
				commands.add(Help.get("back").get());
			}

			Help.executeList(src, commands);
			
			return CommandResult.success();
		}

		if (src.hasPermission("pjp.cmd.portal") && node.getNode("portals").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal")).append(Text.of(" /portal")).build());
		}
		if (src.hasPermission("pjp.cmd.plate") && node.getNode("plates").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:plate")).append(Text.of(" /plate")).build());
		}
		if (src.hasPermission("pjp.cmd.lever") && node.getNode("levers").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:lever")).append(Text.of(" /lever")).build());
		}
		if (src.hasPermission("pjp.cmd.sign") && node.getNode("signs").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:sign")).append(Text.of(" /sign")).build());
		}
		if (src.hasPermission("pjp.cmd.door") && node.getNode("doors").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:door")).append(Text.of(" /door")).build());
		}
		if (src.hasPermission("pjp.cmd.button") && node.getNode("buttons").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:button")).append(Text.of(" /button")).build());
		}
		if (src.hasPermission("pjp.cmd.home") && node.getNode("homes").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:home")).append(Text.of(" /home")).build());
		}
		if (src.hasPermission("pjp.cmd.warp") && node.getNode("warps").getBoolean()) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:warp")).append(Text.of(" /warp")).build());
		}
		if (src.hasPermission("pjp.cmd.back")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:back")).append(Text.of(" /back")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}
