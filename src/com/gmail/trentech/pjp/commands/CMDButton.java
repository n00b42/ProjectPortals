package com.gmail.trentech.pjp.commands;

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

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.ButtonEventManager;
import com.gmail.trentech.pjp.portals.Button;
import com.gmail.trentech.pjp.portals.LocationType;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Resource;

public class CMDButton implements CommandExecutor {

	public CMDButton(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "button").getString();
		
		Help help = new Help("button", " Use this command to create a button that will teleport you to other worlds");
		help.setSyntax(" /button <world> [x] [y] [z]\n /" + alias + " <world> [x] [y] [z]");
		help.setExample(" /button MyWorld\n /button MyWorld -100 65 254\n /button MyWorld random");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/button <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Resource.getBaseName(args.<String>getOne("name").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Resource.getPrettyName(worldName), " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();

		Location<World> spawnLocation;
		LocationType locationType;
		
		if(!args.hasAny("coords")) {
			spawnLocation = world.getSpawnLocation();
			locationType = LocationType.SPAWN;			
		}else{
			String coords = args.<String>getOne("coords").get();
			if(coords.equalsIgnoreCase("random")){				
				spawnLocation = Resource.getRandomLocation(world);
				locationType = LocationType.RANDOM;
			}else{
				locationType = LocationType.NORMAL;
				spawnLocation = Resource.getLocation(world, coords);
				if(spawnLocation == null){
					src.sendMessage(Text.of(TextColors.YELLOW, "/button <world> [x] [y] [z]"));
					return CommandResult.empty();
				}
			}
		}
		
		ButtonEventManager.creators.put(player, new Button(world, spawnLocation, locationType));
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place button to create button portal"));

		return CommandResult.success();
	}
}
