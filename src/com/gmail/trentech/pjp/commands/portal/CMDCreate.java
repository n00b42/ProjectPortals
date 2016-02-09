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
import com.gmail.trentech.pjp.utils.Utils;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "portal").getString();
		
		Help help = new Help("pcreate", "create", " Create a portal to another dimension, or specified location");
		help.setSyntax(" /portal create <name> <world> [x] [y] [z] [direction]\n /" + alias + " create <name> <world> [x] [y] [z] [direction]");
		help.setExample(" /portal create MyPortal MyWorld\n /portal create MyPortal MyWorld -100 65 254\n /portal create MyPortal MyWorld random\n /portal create MyPortal MyWorld -100 65 254 north");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/portal create <name> <world> [x] [y] [z] [direction]"));
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get();
		
		if(Portal.getByName(name).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " already exists"));
			return CommandResult.empty();
		}
		
		if(!args.hasAny("world")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/portal create <name> <world> [x] [y] [z] [direction]"));
			return CommandResult.empty();
		}
		String worldName = Utils.getBaseName(args.<String>getOne("world").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Utils.getPrettyName(worldName), " does not exist"));
			return CommandResult.empty();
		}
		
		String destination = worldName + ":";
		
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
					src.sendMessage(Text.of(TextColors.YELLOW, "/portal create <world> [x] [y] [z] [direction]"));
					return CommandResult.empty();
				}

				if(coords.length == 4){
					rotation = Rotation.get(coords[3]);
					
					if(rotation.isPresent()){
						destination = worldName + ":" + x + "." + y + "." + z + ":" + rotation.get().getName();
					}else{
						src.sendMessage(Text.of(TextColors.YELLOW, "/portal create <world> [x] [y] [z] [direction]"));
						return CommandResult.empty();
					}
				}else{
					destination = worldName + ":" + x + "." + y + "." + z;	
				}
			}
		}else{
			destination = worldName + ":spawn";
		}
		
		PortalListener.builders.put(player, new PortalBuilder(destination).name(name));
		
		player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by "))
				.onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
		
		return CommandResult.success();
	}
}
