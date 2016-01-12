package com.gmail.trentech.pjp.commands.cube;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portals.Cuboid;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Resource;

public class CMDRemove implements CommandExecutor {

	public CMDRemove(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "portal").getString();
		
		Help help = new Help("cremove", " Remove an existing portal");
		help.setSyntax(" /portal remove <name>\n /" + alias + " r <name>");
		help.setExample(" /portal remove MyPortal");
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
			src.sendMessage(Text.of(TextColors.YELLOW, "/cube remove <name>"));
			return CommandResult.empty();
		}
		String name = Resource.getBaseName(args.<String>getOne("name").get());

		if(!Cuboid.getByName(name).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " does not exist"));
			return CommandResult.empty();
		}
		
		Cuboid.remove(name);

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Cube ", name, " has been removed"));

		return CommandResult.success();
	}
}