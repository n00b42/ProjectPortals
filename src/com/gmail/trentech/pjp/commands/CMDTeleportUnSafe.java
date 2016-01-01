package com.gmail.trentech.pjp.commands;

import java.util.HashMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CMDTeleportUnSafe implements CommandExecutor {

	public static HashMap<Player, Location<World>> players = new HashMap<>();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			return CommandResult.success();
		}
		Player player = (Player) src;
		
		if(!players.containsKey(player)){
			return CommandResult.success();
		}
		Location<World> location = players.get(player);
		
		player.setLocation(location);
		player.sendTitle(Title.of(Text.of(TextColors.GOLD, location.getExtent().getName()), Text.of(TextColors.DARK_PURPLE, "x: ", location.getExtent().getSpawnLocation().getBlockX(), ", y: ", location.getExtent().getSpawnLocation().getBlockY(),", z: ", location.getExtent().getSpawnLocation().getBlockZ())));
		
		players.remove(player);
		
		return CommandResult.success();
	}

}
