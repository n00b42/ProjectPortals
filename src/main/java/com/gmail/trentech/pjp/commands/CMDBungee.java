package com.gmail.trentech.pjp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CMDBungee implements CommandExecutor {

	public CommandSpec cmdBungee = CommandSpec.builder().arguments(
		GenericArguments.string(Text.of("name")),
		GenericArguments.string(Text.of("destination")),
		GenericArguments.flags().flag("b")
			.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
			.valueFlag(GenericArguments.string(Text.of("direction")), "d")
			.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none())
	).permission("pjp.cmd.bungee").executor(this).build();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		
		
//		if(!(src instanceof Player)){
//			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
//			return CommandResult.empty();
//		}
//		Player player = (Player) src;
//
//		Consumer<List<String>> consumer = (list) -> {
//			System.out.println("Listing Servers");
//			for(String s : list) {
//				System.out.println(s);
//			}
//		};
//
//		Spongee.API.getServerList(consumer, player);
//		
//		Consumer<String> consumer1 = (server) -> {
//			System.out.println("serverName");
//			System.out.println(server);
//		};
//		
//		Spongee.API.getServerName(consumer1, player);
//		
//		IntConsumer consumer2 = (count) -> {
//			System.out.println("global player count");
//			System.out.println(count);
//		};
//		
//		Spongee.API.getGlobalPlayerCount(consumer2, player);
//		
//		Consumer<InetSocketAddress> consumer3 = (ip) -> {
//			System.out.println("ip address");
//			System.out.println(ip.getAddress().toString());
//		};
//		
//		Spongee.API.getIP(player, consumer3);
//		
//		IntConsumer consumer4 = (count) -> {
//			System.out.println("player count");
//			System.out.println(count);
//		};
//		
//		Spongee.API.getPlayerCount("lobby", consumer4, player);
//		
//		Consumer<UUID> consumer5 = (uuid) -> {
//			System.out.println("uuid");
//			System.out.println(uuid.toString() + " " + player.getUniqueId().toString());
//		};
//		
//		Spongee.API.getRealUUID(player, consumer5);
//		
//		Consumer<List<String>> consumer6 = (list) -> {
//			System.out.println("Listing Players");
//			for(String s : list) {
//				System.out.println(s);
//			}
//		};
//		
//		Spongee.API.listAllPlayers(consumer6, player);
//		
//		Spongee.API.sendMessage("MonroeTT", Text.of(TextColors.RED, "This is a bungee test"), player);
		//Spongee.API.kickPlayer("MonroeTT", Text.of(TextColors.RED, "This is a kick test"), player);
		//Spongee.API.connectPlayer(player, "server1");

		return CommandResult.success();
	}
}
