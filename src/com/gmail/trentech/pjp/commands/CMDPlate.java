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
import com.gmail.trentech.pjp.listeners.PlateEventManager;
import com.gmail.trentech.pjp.portals.LocationType;
import com.gmail.trentech.pjp.portals.Plate;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Resource;

public class CMDPlate implements CommandExecutor {

	public CMDPlate(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "plate").getString();
		
		Help help = new Help("plate", " Use this command to create a pressure plate that will teleport you to other worlds");
		help.setSyntax(" /plate <world> [x] [y] [z]\n /" + alias + " <world> [x] [y] [z]");
		help.setExample(" /plate MyWorld\n /plate MyWorld -100 65 254\n /plate MyWorld random");
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
			src.sendMessage(Text.of(TextColors.GOLD, "/plate <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Resource.getBaseName(args.<String>getOne("name").get());

		if(!Main.getGame().getServer().getWorld(Resource.getBaseName(worldName)).isPresent()){
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
					src.sendMessage(Text.of(TextColors.YELLOW, "/plate <world> [x] [y] [z]"));
					return CommandResult.empty();
				}
			}
		}
		
		PlateEventManager.creators.put(player, new Plate(world, spawnLocation, locationType));

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place pressure plate to create presure plate portal"));

		return CommandResult.success();
	}
}
