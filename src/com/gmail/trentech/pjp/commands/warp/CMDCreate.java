package com.gmail.trentech.pjp.commands.warp;

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

import com.gmail.trentech.pjp.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDCreate implements CommandExecutor {

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
		
		ConfigManager configManager = new ConfigManager("warps.conf");
		ConfigurationNode config = configManager.getConfig();
		
		if(config.getNode("Warps", warpName).getString() != null){
			src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " already exists."));
			return CommandResult.empty();
		}
		
		String worldName = player.getWorld().getName();
		
		Location<World> location = player.getLocation();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		config.getNode("Warps", warpName, "World").setValue(worldName);
		config.getNode("Warps", warpName, "X").setValue(x);
		config.getNode("Warps", warpName, "Y").setValue(y);
		config.getNode("Warps", warpName, "Z").setValue(z);
		
		configManager.save();
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp ", warpName, " create"));

		return CommandResult.success();
	}
}
