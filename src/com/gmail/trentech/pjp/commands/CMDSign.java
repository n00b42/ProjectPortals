package com.gmail.trentech.pjp.commands;

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
import com.gmail.trentech.pjp.utils.Utils;

public class CMDSign implements CommandExecutor {

	public CMDSign(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "sign").getString();
		
		Help help = new Help("sign", "sign", " Use this command to create a sign that will teleport you to other worlds");
		help.setSyntax(" /sign <world> [x] [y] [z]\n /" + alias + " <world> [x] [y] [z]");
		help.setExample(" /sign MyWorld\n /sign MyWorld -100 65 254\n /sign MyWorld random");
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
			String coords = args.<String>getOne("coords").get();
			if(coords.equalsIgnoreCase("random")){
				portalData = new PortalData("", world, true);
			}else{
				String[] testInt = coords.split(" ");
				try{
					int x = Integer.parseInt(testInt[0]);
					int y = Integer.parseInt(testInt[1]);
					int z = Integer.parseInt(testInt[2]);
					
					portalData = new PortalData("", world.getLocation(x, y, z));
				}catch(Exception e){
					src.sendMessage(Text.of(TextColors.YELLOW, "/sign <world> [x] [y] [z]"));
					return CommandResult.empty();
				}
			}
		}else{
			portalData = new PortalData("", world, false);
		}
		
		SignListener.creators.put(player, portalData);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place sign to create sign portal"));

		return CommandResult.success();
	}
}
