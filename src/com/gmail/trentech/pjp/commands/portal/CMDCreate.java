package com.gmail.trentech.pjp.commands.portal;

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
import com.gmail.trentech.pjp.utils.Utils;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "portal").getString();
		
		Help help = new Help("pcreate", "create", " Create a portal to another dimension, or specified location");
		help.setSyntax(" /portal create <name> <world> [x] [y] [z]\n /" + alias + " create <name> <world> [x] [y] [z]");
		help.setExample(" /portal create MyPortal MyWorld\n /portal create MyPortal MyWorld -100 65 254\n /portal create MyPortal MyWorld random");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/portal create <name> <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get();
		
		if(Portal.getByName(name).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " already exists"));
			return CommandResult.empty();
		}
		
		if(!args.hasAny("world")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/portal create <name> <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Utils.getBaseName(args.<String>getOne("world").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Utils.getPrettyName(worldName), " does not exist"));
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
		
		PortalListener.getBuilders().put(player, new PortalBuilder(destination).name(name));
		
		player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by "))
				.onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
		
		return CommandResult.success();
	}
}
