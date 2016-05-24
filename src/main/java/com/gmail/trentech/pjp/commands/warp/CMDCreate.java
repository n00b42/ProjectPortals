package com.gmail.trentech.pjp.commands.warp;

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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.object.Warp;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;

import flavor.pie.spongee.Spongee;

public class CMDCreate implements CommandExecutor {

	private boolean exist = true;
	
	public CMDCreate() {
		Help help = new Help("wcreate", "create", " Use this command to create a warp that will teleport you to other worlds");
		help.setSyntax(" /warp create <name> [<destination> [-c <x,y,z>] [-d <direction>] [-b]] [-p <price>]\n /w <name> [<destination> [-c <x,y,z>] [-d <direction>] [-b]] [-p <price>]");
		help.setExample(" /warp create Lobby\n /warp create Lobby MyWorld\n /warp create Lobby MyWorld -c -100,65,254\n /warp create Random MyWorld -c random\n /warp create Lobby MyWorld -c -100,65,254 -d south\n /warp create Lobby MyWorld -d southeast\n /warp Lobby MyWorld -p 50\n /warp Lobby -p 50");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get().toLowerCase();

		if(name.equalsIgnoreCase("-c") || name.equalsIgnoreCase("-d") || name.equalsIgnoreCase("-p")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		
		if(Warp.get(name).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " already exists"));
			return CommandResult.empty();
		}
		
		String worldName = player.getWorld().getName();		
		String destination = worldName + ":spawn";
		Rotation rotation = Rotation.EAST;
		boolean bungee = false;
		
		if(args.hasAny("destination")) {
			if (args.hasAny("b")) {
				bungee = args.hasAny("b");
				
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
				worldName = args.<String>getOne("destination").get();

				if(worldName.equalsIgnoreCase("-c") || worldName.equalsIgnoreCase("-d") || worldName.equalsIgnoreCase("-p")) {
					src.sendMessage(invalidArg());
					return CommandResult.empty();
				}
				
				if(!Main.getGame().getServer().getWorld(worldName).isPresent()) {
					src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is not loaded or does not exist"));
					return CommandResult.empty();
				}
				destination = worldName + ":spawn";;
				
				if(args.hasAny("x,y,z")) {
					String[] coords = args.<String>getOne("x,y,z").get().split(",");

					if(coords[0].equalsIgnoreCase("random")) {
						destination = destination.replace("spawn", "random");
					}else{
						int x;
						int y;
						int z;
						
						try{
							x = Integer.parseInt(coords[0]);
							y = Integer.parseInt(coords[1]);
							z = Integer.parseInt(coords[2]);				
						}catch(Exception e) {
							src.sendMessage(Text.of(TextColors.RED, "Incorrect coordinates"));
							src.sendMessage(invalidArg());
							return CommandResult.empty();
						}
						destination = destination.replace("spawn", x + "." + y + "." + z);
					}
				}

				if(args.hasAny("direction")) {
					String direction = args.<String>getOne("direction").get();
					
					Optional<Rotation> optionalRotation = Rotation.get(direction);
					
					if(!optionalRotation.isPresent()) {
						src.sendMessage(Text.of(TextColors.RED, "Incorrect direction"));
						src.sendMessage(invalidArg());
						return CommandResult.empty();
					}

					rotation = optionalRotation.get();
				}
			}
		}else {
			Location<World> location = player.getLocation();
			destination = worldName + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
			rotation = Rotation.getClosest(player.getRotation().getFloorY());
		}

		double price = 0;
		
		if(args.hasAny("price")) {
			try{
				price = Double.parseDouble(args.<String>getOne("price").get());
			}catch(Exception e) {
				src.sendMessage(Text.of(TextColors.RED, "Incorrect price"));
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		new Warp(name, destination, rotation.getName(), price, bungee).create();

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", name, " create"));

		return CommandResult.success();
	}
	
	private Text invalidArg() {
		Text t1 = Text.of(TextColors.RED, "Usage: /warp create <name> [<destination> [-c <x,y,z>] ");
		Text t2 = Text.builder().color(TextColors.RED).onHover(TextActions.showText(Text.of("NORTH\nNORTHEAST\nEAST\nSOUTHEAST\nSOUTH\nSOUTHWEST\nWEST\nNORTHWEST"))).append(Text.of("[-d <direction>]> ")).build();
		Text t3 = Text.of(TextColors.RED, "[-b]] [-p <price>]");
		return Text.of(t1,t2,t3);
	}
}
