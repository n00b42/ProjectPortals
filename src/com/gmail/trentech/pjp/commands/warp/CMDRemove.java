package com.gmail.trentech.pjp.commands.warp;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDRemove implements CommandExecutor {

	public CMDRemove(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "warp").getString();
		
		Help help = new Help("wremove", " Remove an existing  warp point");
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
		
		ConfigManager configManager = new ConfigManager("warps.conf");
		ConfigurationNode config = configManager.getConfig();
		
		if(config.getNode("Warps", warpName).getString() == null){
			src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " does not exist"));
			return CommandResult.empty();
		}
		
		config.getNode("Warps").removeChild(warpName);

		configManager.save();
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", warpName, " removed"));

		return CommandResult.success();
	}
}
