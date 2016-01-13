package com.gmail.trentech.pjp.commands.cube;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.CuboidListener;
import com.gmail.trentech.pjp.portals.builders.CuboidBuilder;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Resource;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "cube").getString();
		
		Help help = new Help("ccreate", "create", "Create cuboid portal to another dimension, or specified location. No arguments allow for deleting portals");
		help.setSyntax(" /cube <world> [x] [y] [z]\n /" + alias + " <world> [x] [y] [z]");
		help.setExample(" /cube MyWorld\n /cube MyWorld -100 65 254\n /cube MyWorld random\n /cube show\n /cube remove");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/cube create <name> <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String name = Resource.getBaseName(args.<String>getOne("name").get());
		
		if(!args.hasAny("world")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/cube create <name> <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Resource.getBaseName(args.<String>getOne("world").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Resource.getPrettyName(worldName), " does not exist"));
			return CommandResult.empty();
		}
		
		String destination = worldName + ":";
		
		if(args.hasAny("coords")) {
			String coords = args.<String>getOne("coords").get();
			if(coords.equalsIgnoreCase("random")){
				destination = destination + "random";
			}else{
				try{
					String[] testInt = coords.split(" ");
					Integer.parseInt(testInt[0]);
					Integer.parseInt(testInt[1]);
					Integer.parseInt(testInt[2]);
				}catch(Exception e){
					src.sendMessage(Text.of(TextColors.YELLOW, "/portal <name> <world> [x] [y] [z]"));
					return CommandResult.empty();
				}
				destination = destination + coords.replace(" ", ".");
			}
		}else{
			destination = destination + "spawn";
		}
		
		CuboidListener.getBuilders().put(player, new CuboidBuilder(destination).name(name));
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click starting point"));

		return CommandResult.success();
	}
}
