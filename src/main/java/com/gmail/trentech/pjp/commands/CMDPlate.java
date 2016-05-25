package com.gmail.trentech.pjp.commands;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
import com.gmail.trentech.pjp.data.object.Plate;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;

import flavor.pie.spongee.Spongee;

public class CMDPlate implements CommandExecutor {

	private boolean exist = true;
	
	public CMDPlate(){
		Help help = new Help("plate", "plate", " Use this command to create a plate that will teleport you to other worlds");
		help.setSyntax(" /plate <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]\n /pp <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]");
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

		if(!args.hasAny("destination")) {
			src.sendMessage(getUsage());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("destination").get();

		if(worldName.equalsIgnoreCase("-c") || worldName.equalsIgnoreCase("-d") || worldName.equalsIgnoreCase("-p") || worldName.equalsIgnoreCase("-b")) {
			src.sendMessage(getUsage());
			return CommandResult.empty();
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is not loaded or does not exist"));
			return CommandResult.empty();
		}

		String destination = worldName + ":spawn";
		Rotation rotation = Rotation.EAST;
		boolean bungee = args.hasAny("b");
		
		if(bungee) {
			String server = args.<String>getOne("destination").get();

			Consumer<List<String>> consumer = (list) -> {
				if(!list.contains(server)) {
					player.sendMessage(Text.of(TextColors.DARK_RED, server, " is offline or not correctly configured for Bungee"));
					exist = false;
				}
			};
			
			Spongee.API.getServerList(consumer, player);
			
			if(!exist) {
				return CommandResult.empty();
			}
			
			destination = server;
		}else {
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
						src.sendMessage(getUsage());
						return CommandResult.empty();
					}
					destination = destination.replace("spawn", x + "." + y + "." + z);
				}
			}

			if(args.hasAny("direction")){
				String direction = args.<String>getOne("direction").get();
				
				Optional<Rotation> optionalRotation = Rotation.get(direction);
				
				if(!optionalRotation.isPresent()){
					src.sendMessage(Text.of(TextColors.RED, "Incorrect direction"));
					src.sendMessage(getUsage());
					return CommandResult.empty();
				}

				rotation = optionalRotation.get();
			}
		}
		
		double price = 0;
		
		if(args.hasAny("price")){
			try{
				price = Double.parseDouble(args.<String>getOne("price").get());
			}catch(Exception e){
				src.sendMessage(Text.of(TextColors.RED, "Incorrect price"));
				src.sendMessage(getUsage());
				return CommandResult.empty();
			}
		}
		
		PlateListener.builders.put(player.getUniqueId(), new Plate(destination, rotation.getName(), price, args.hasAny("b")));

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place plate to create plate portal"));

		return CommandResult.success();
	}
	
	private Text getUsage() {
		Text usage = Text.of(TextColors.RED, "Usage: /plate");
		
		usage = Text.join(usage, Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("Enter a world or bungee server"))).append(Text.of(" <destination>")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("Use this flag if <destination> is a bungee server"))).append(Text.of(" [-b]")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("Enter x y z coordinates or \"random\""))).append(Text.of(" [-c <x,y,z>]")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("NORTH\nNORTHEAST\nEAST\nSOUTHEAST\nSOUTH\nSOUTHWEST\nWEST\nNORTHWEST"))).append(Text.of(" [-d <direction>]")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("Enter the cost to use portal or 0 to disable"))).append(Text.of(" [-p price]")).build());
		
		return usage;
	}
}
