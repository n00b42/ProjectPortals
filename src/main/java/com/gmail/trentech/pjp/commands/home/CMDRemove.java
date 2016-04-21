package com.gmail.trentech.pjp.commands.home;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRemove implements CommandExecutor {

	public CMDRemove(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "home").getString();
		
		Help help = new Help("hremove", "remove", "Remove an existing home");
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
			src.sendMessage(Text.of(TextColors.RED, "Usage: /home remove <name>"));
			return CommandResult.empty();
		}
		String homeName = args.<String>getOne("name").get();
		
		HomeData homeData;

		Optional<HomeData> optionalHomeData = player.get(HomeData.class);
		
		if(optionalHomeData.isPresent()){
			homeData = optionalHomeData.get();
		}else{
			homeData = new HomeData();
		}

		if(!homeData.getDestination(homeName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, homeName, " does not exist"));
			return CommandResult.empty();
		}
		
		homeData.removeHome(homeName);
		
		player.offer(homeData);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", homeName, " removed"));

		return CommandResult.success();
	}
}
