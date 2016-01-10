package com.gmail.trentech.pjp.commands.home;

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

import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "home").getString();
		
		Help help = new Help("hcreate", " Create a new home");
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
		
		ConfigManager configManager = new ConfigManager("Players", player.getUniqueId().toString() + ".conf");
		ConfigurationNode config = configManager.getConfig();
		
		int defaultAmount = new ConfigManager().getConfig().getNode("Options", "Homes").getInt();

		int amount = 0;
		if(config.getNode("Amount").getString() != null){
			amount = config.getNode("Amount").getInt();
		}
		
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
		
		if(config.getNode("Homes", homeName).getString() != null){
			src.sendMessage(Text.of(TextColors.DARK_RED, homeName, " already exists."));
			return CommandResult.empty();
		}
		
		String worldName = player.getWorld().getName();
		
		Location<World> location = player.getLocation();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		config.getNode("Homes", homeName, "World").setValue(worldName);
		config.getNode("Homes", homeName, "X").setValue(x);
		config.getNode("Homes", homeName, "Y").setValue(y);
		config.getNode("Homes", homeName, "Z").setValue(z);
		config.getNode("Amount").setValue(amount);

		configManager.save();
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", homeName, " create"));

		return CommandResult.success();
	}
}
