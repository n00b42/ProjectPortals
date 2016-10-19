package com.gmail.trentech.pjp.commands.portal;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Help;

public class CMDPortal implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
			Help.executeList(src, Help.get("portal").get().getChildren());
			
			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pjp.cmd.portal.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal create")).append(Text.of(" /portal create")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal remove")).append(Text.of(" /portal remove")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.rename")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal rename")).append(Text.of(" /portal rename")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.destination")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal destination")).append(Text.of(" /portal destination")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal list")).append(Text.of(" /portal list")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.save")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(" /portal save")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.particle")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal particle")).append(Text.of(" /portal particle")).build());
		}
		if (src.hasPermission("pjp.cmd.portal.price")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjp:portal price")).append(Text.of(" /portal price")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

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
