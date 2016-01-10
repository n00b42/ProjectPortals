package com.gmail.trentech.pjp.commands.home;

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
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "home").getString();
		
		Help help = new Help("hremove", " Remove an existing home");
		help.setSyntax(" /home remove <name>\n /" + alias + " r <name>");
		help.setExample(" /home remove OldHome");
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
		String homeName = args.<String>getOne("name").get();
		
		ConfigManager configManager = new ConfigManager("Players", player.getUniqueId().toString() + ".conf");
		ConfigurationNode config = configManager.getConfig();
		
		if(config.getNode("Homes", homeName).getString() == null){
			src.sendMessage(Text.of(TextColors.DARK_RED, homeName, " does not exist"));
			return CommandResult.empty();
		}
		
		config.getNode("Amount").setValue(config.getNode("Amount").getInt() - 1);
		config.getNode("Homes").removeChild(homeName);

		configManager.save();
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", homeName, " removed"));

		return CommandResult.success();
	}
}
