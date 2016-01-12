package com.gmail.trentech.pjp.commands.portal;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.portals.builders.PortalBuilder;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDSave implements CommandExecutor {

	public CMDSave(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "portal").getString();
		
		Help help = new Help("save", " Saves generated portal");
		help.setSyntax(" /portal save\n /" + alias + " s");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if(!PortalListener.getBuilders().containsKey(player)){
			return CommandResult.empty();
		}
		PortalBuilder builder = (PortalBuilder) PortalListener.getBuilders().get(player);

		if(builder.build()){
			PortalListener.getBuilders().remove(player);
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal ", builder.getName(), " created successfully"));
		}
		
		return CommandResult.success();
	}
}
