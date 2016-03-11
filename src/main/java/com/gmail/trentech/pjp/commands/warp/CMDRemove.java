package com.gmail.trentech.pjp.commands.warp;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portals.Warp;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRemove implements CommandExecutor {

	public CMDRemove(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "warp").getString();
		
		Help help = new Help("wremove", "remove", " Remove an existing  warp point");
		help.setSyntax(" /warp remove <name>\n /" + alias + " r <name>");
		help.setExample(" /warp remove OldSpawn");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/home remove <name>"));
			return CommandResult.empty();
		}
		String warpName = args.<String>getOne("name").get();
		
		if(!Warp.get(warpName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " does not exist"));
			return CommandResult.empty();
		}
		
		Warp.remove(warpName);

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", warpName, " removed"));

		return CommandResult.success();
	}
}
