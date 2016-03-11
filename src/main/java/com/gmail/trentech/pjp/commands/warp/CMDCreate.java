package com.gmail.trentech.pjp.commands.warp;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.portals.Warp;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "warp").getString();
		
		Help help = new Help("wcreate", "create", " Create a new warp point");
		help.setSyntax(" /warp create <name>\n /" + alias + " c <name>");
		help.setExample(" /warp create Spawn");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/warp create <name>"));
			return CommandResult.empty();
		}
		String warpName = args.<String>getOne("name").get();
		
		Optional<Warp> optionalWarp = Warp.get(warpName);
		
		if(optionalWarp.isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " already exists."));
			return CommandResult.empty();
		}
		
		Location<World> location = player.getLocation();
		
		String destination = player.getWorld().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ() + ":" + Rotation.getClosest(player.getRotation().getFloorY());
		
		Warp.save(warpName, destination);

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", warpName, " create"));

		return CommandResult.success();
	}
}
