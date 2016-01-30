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
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.mutable.PortalData;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.Utils;

public class CMDSign implements CommandExecutor {

	public CMDSign(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "sign").getString();
		
		Help help = new Help("sign", "sign", " Use this command to create a sign that will teleport you to other worlds");
		help.setSyntax(" /sign <world> [x] [y] [z]\n /" + alias + " <world> [x] [y] [z]");
		help.setExample(" /sign MyWorld\n /sign MyWorld -100 65 254\n /sign MyWorld random\n /sign MyWorld -100 65 254 east\n /sign MyWorld northeast");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/sign <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Utils.getBaseName(args.<String>getOne("name").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Utils.getPrettyName(worldName), " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		PortalData portalData;
		
		if(args.hasAny("coords")) {
			String[] coords = args.<String>getOne("coords").get().split(" ");
			Optional<Rotation> rotation = Rotation.get(coords[0]);
			
			if(rotation.isPresent()){
				portalData = new PortalData("", world, rotation.get());
			}else if(coords[0].equalsIgnoreCase("random")){
				portalData = new PortalData("", world, true);
			}else{
				int x;
				int y;
				int z;
				
				try{
					String[] vector = coords[1].split(".");
					
					x = Integer.parseInt(vector[0]);
					y = Integer.parseInt(vector[1]);
					z = Integer.parseInt(vector[2]);				
				}catch(Exception e){
					src.sendMessage(Text.of(TextColors.YELLOW, "/sign <world> [x] [y] [z] [direction]"));
					return CommandResult.empty();
				}
				
				if(coords.length == 3){
					rotation = Rotation.get(coords[2]);
					
					if(rotation.isPresent()){
						portalData = new PortalData("", world.getLocation(x, y, z), rotation.get());
					}else{
						src.sendMessage(Text.of(TextColors.YELLOW, "/sign <world> [x] [y] [z] [direction]"));
						return CommandResult.empty();
					}
				}else{
					portalData = new PortalData("", world.getLocation(x, y, z));
				}
			}
		}else{
			portalData = new PortalData("", world, false);
		}
		
		SignListener.builders.put(player, portalData);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place sign to create sign portal"));

		return CommandResult.success();
	}
}
