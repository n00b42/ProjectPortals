package com.gmail.trentech.pjp.commands;

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

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.portals.Plate;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;

public class CMDPlate implements CommandExecutor {

	public CMDPlate(){
		Help help = new Help("plate", "plate", " Use this command to create a plate that will teleport you to other worlds");
		help.setSyntax(" /plate <world> [-c <x,y,z>] [-d <direction>] [-p <price>]\n /pp plate <world> [-c <x,y,z>] [-d <direction>] [-p <price>]");
		help.setExample(" /plate MyWorld\n /plate MyWorld -c -100,65,254\n /plate MyWorld -c random\n /plate MyWorld -c -100,65,254 -d south\n /plate MyWorld -d southeast\n /plate MyWorld -p 50");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if(!args.hasAny("world")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("world").get();

		if(worldName.equalsIgnoreCase("-c") || worldName.equalsIgnoreCase("-d") || worldName.equalsIgnoreCase("-p")){
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		
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
					src.sendMessage(invalidArg());
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
				src.sendMessage(invalidArg());
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
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}
		
		PlateListener.builders.put(player, new Plate(destination, rotation, price));

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place plate to create plate portal"));

		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.RED, "Usage: /plate <world> [-c <x,y,z>] ");
		Text t2 = Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("NORTH\nNORTHEAST\nEAST\nSOUTHEAST\nSOUTH\nSOUTHWEST\nWEST\nNORTHWEST"))).append(Text.of("[-d <direction>] ")).build();
		Text t3 = Text.of(TextColors.RED, "[-p <price>]");
		return Text.of(t1,t2,t3);
	}
}
