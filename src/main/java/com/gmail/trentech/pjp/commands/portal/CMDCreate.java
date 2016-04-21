package com.gmail.trentech.pjp.commands.portal;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.portals.builders.PortalBuilder;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "portal").getString();
		
		Help help = new Help("pcreate", "create", " Use this command to create a portal that will teleport you to other worlds");
		help.setSyntax(" /portal create [<name>] [<world>] [-c <x,y,z>] [-d <direction>] [-p <price>]\n /" + alias + " portal [<name>] [<world>] [-c <x,y,z>] [-d <direction>] [-p <price>]");
		help.setExample(" /portal create MyPortal MyWorld\n /portal create MyPortal MyWorld -c -100,65,254\n /portal create MyPortal MyWorld -c random\n /portal create MyPortal MyWorld -c -100 65 254 -d south\n /portal create MyPortal MyWorld -d southeast\n /portal create MyPortal MyWorld -p 50");
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
			src.sendMessage(Text.of(TextColors.RED, "Usage: /portal create [<name>] [<world>] [-c <x,y,z>] [-d <direction>] [-p <price>]"));
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get();
		
		if(Portal.getByName(name).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " already exists"));
			return CommandResult.empty();
		}
		
		if(!args.hasAny("world")) {
			src.sendMessage(Text.of(TextColors.RED, "Usage: /portal create [<name>] [<world>] [-c <x,y,z>] [-d <direction>] [-p <price>]"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("world").get();

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is not loaded or does not exist"));
			return CommandResult.empty();
		}

		String destination = worldName + ":spawn";
		
		if(args.hasAny("x,y,z")){
			String[] coords = args.<String>getOne("x,y,z").get().split(",");

			if(coords[0].equalsIgnoreCase("random")){
				destination = destination.replace("spawn", "random");
			}else{
				int x;
				int y;
				int z;
				
				try{
					x = Integer.parseInt(coords[0]);
					y = Integer.parseInt(coords[1]);
					z = Integer.parseInt(coords[2]);				
				}catch(Exception e){
					src.sendMessage(Text.of(TextColors.RED, "Incorrect coordinates"));
					src.sendMessage(Text.of(TextColors.RED, "Usage: /portal create [<name>] [<world>] [-c <x,y,z>]"));
					return CommandResult.empty();
				}
				destination = destination.replace("spawn", x + "." + y + "." + z);
			}
		}
		
		Rotation rotation = Rotation.EAST;
		
		if(args.hasAny("direction")){
			String direction = args.<String>getOne("direction").get();
			
			Optional<Rotation> optionalRotation = Rotation.get(direction);
			
			if(!optionalRotation.isPresent()){
				src.sendMessage(Text.of(TextColors.RED, "Incorrect direction"));
				src.sendMessage(Text.of(TextColors.RED, "Usage: /portal create [<world>] [-d <direction>]"));
				return CommandResult.empty();
			}

			rotation = optionalRotation.get();
		}
		
		double price = 0;
		
		if(args.hasAny("price")){
			try{
				price = Double.parseDouble(args.<String>getOne("price").get());
			}catch(Exception e){
				src.sendMessage(Text.of(TextColors.RED, "Incorrect price"));
				src.sendMessage(Text.of(TextColors.RED, "Usage: /portal create [<world>] [-p <price>]"));
				return CommandResult.empty();
			}
		}
		
		PortalListener.builders.put(player, new PortalBuilder(destination, rotation, price).name(name));
		
		player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by "))
				.onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
		
		return CommandResult.success();
	}
}
