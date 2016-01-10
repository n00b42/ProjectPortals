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
import com.gmail.trentech.pjp.portals.CuboidBuilder;
import com.gmail.trentech.pjp.portals.LocationType;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Resource;

public class CMDCube implements CommandExecutor {

	public CMDCube(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "cube").getString();
		
		Help help = new Help("cube", " Create cuboid portal to another dimension, or specified location. No arguments allow for deleting portals");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/cube <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Resource.getBaseName(args.<String>getOne("name").get());

		if(worldName.equalsIgnoreCase("remove")){
			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click the Cuboid to remove"));
			CuboidBuilder.getActiveBuilders().put((Player) src, new CuboidBuilder());
			return CommandResult.success();
		}
		
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
					src.sendMessage(Text.of(TextColors.YELLOW, "/cube <world> [x] [y] [z]"));
					return CommandResult.empty();
				}
			}
		}

		CuboidBuilder builder = new CuboidBuilder(world, spawnLocation, locationType);

		CuboidBuilder.getActiveBuilders().put(player, builder);

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click starting point"));

		return CommandResult.success();
	}

}
