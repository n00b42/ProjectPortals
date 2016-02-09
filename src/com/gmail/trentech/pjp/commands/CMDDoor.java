package com.gmail.trentech.pjp.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.Utils;

public class CMDDoor implements CommandExecutor {

	public CMDDoor(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "door").getString();
		
		Help help = new Help("door", "door", " Use this command to create a door that will teleport you to other worlds");
		help.setSyntax(" /door <world> [x] [y] [z] [direction]\n /" + alias + " <world> [x] [y] [z] [direction]");
		help.setExample(" /door MyWorld\n /door MyWorld -100 65 254\n /door MyWorld random\n /door MyWorld  -100 65 254 southeast\n /door MyWorld east");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/door <world> [x] [y] [z] [direction]"));
			return CommandResult.empty();
		}
		String worldName = Utils.getBaseName(args.<String>getOne("name").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Utils.getPrettyName(worldName), " does not exist"));
			return CommandResult.empty();
		}

		String destination;
		
		if(args.hasAny("coords")) {
			String[] coords = args.<String>getOne("coords").get().split(" ");
			Optional<Rotation> rotation = Rotation.get(coords[0]);
			
			if(rotation.isPresent()){
				destination = worldName + ":spawn:" + rotation.get().getName();
			}else if(coords[0].equalsIgnoreCase("random")){
				destination = worldName + ":random";
			}else{
				int x;
				int y;
				int z;
				
				try{
					x = Integer.parseInt(coords[0]);
					y = Integer.parseInt(coords[1]);
					z = Integer.parseInt(coords[2]);				
				}catch(Exception e){
					src.sendMessage(Text.of(TextColors.YELLOW, "/door <world> [x] [y] [z] [direction]"));
					return CommandResult.empty();
				}

				if(coords.length == 4){
					rotation = Rotation.get(coords[3]);
					
					if(rotation.isPresent()){
						destination = worldName + ":" + x + "." + y + "." + z + ":" + rotation.get().getName();
					}else{
						src.sendMessage(Text.of(TextColors.YELLOW, "/door <world> [x] [y] [z] [direction]"));
						return CommandResult.empty();
					}
				}else{
					destination = worldName + ":" + x + "." + y + "." + z;	
				}
			}
		}else{
			destination = worldName + ":spawn";
		}
		
		DoorListener.builders.put(player, destination);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place door to create door portal"));

		return CommandResult.success();
	}
}
