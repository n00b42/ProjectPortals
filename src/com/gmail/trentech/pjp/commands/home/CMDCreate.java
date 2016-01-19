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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.home.HomeData;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "home").getString();
		
		Help help = new Help("hcreate", "create", " Create a new home");
		help.setSyntax(" /home create <name>\n /" + alias + " c <name>");
		help.setExample(" /home create MyHome");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/home create <name>"));
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

		int defaultAmount = new ConfigManager().getConfig().getNode("Options", "Homes").getInt();

		int amount = homeData.homes().get().size();

		int extra = 0;
		for(int i = 1; i <= 100; i++){
			if(player.hasPermission("pjp.homes." + i)){
				extra = i;
				break;
			}
		}
		
		if(!player.hasPermission("pjp.homes.unlimited")){
			if(amount >= (defaultAmount + extra)){
				src.sendMessage(Text.of(TextColors.DARK_RED, "You have reached the maximum number of homes you can have"));
				return CommandResult.empty();
			}
			amount++;
		}
		if(homeData.getHome(homeName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, homeName, " already exists."));
			return CommandResult.empty();
		}

		Location<World> location = player.getLocation();

		homeData.addHome(homeName, location);
		
		player.offer(homeData);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", homeName, " create"));

		return CommandResult.success();
	}
}
