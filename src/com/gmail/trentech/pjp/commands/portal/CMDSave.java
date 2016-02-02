package com.gmail.trentech.pjp.commands.portal;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.portals.builders.PortalBuilder;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDSave implements CommandExecutor {

	public CMDSave(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "portal").getString();
		
		Help help = new Help("save", "save", " Saves generated portal");
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

		if(!PortalListener.builders.containsKey(player)){
			return CommandResult.empty();
		}
		PortalBuilder builder = (PortalBuilder) PortalListener.builders.get(player);

		if(!builder.isFill()){
			builder.fill(true);
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal frame saved"));
			player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin filling in portal frame, followed by "))
					.onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
			return CommandResult.success();
		}
		
		if(builder.build()){
			Main.getGame().getScheduler().createTaskBuilder().name("PJP" + builder.getName()).delayTicks(20).execute(new Runnable(){

				@Override
				public void run() {
					PortalListener.builders.remove(player);
				}
				
			}).submit(Main.getPlugin());
			
			player.playSound(SoundTypes.AMBIENCE_THUNDER, player.getLocation().getPosition(), 1);
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal ", builder.getName(), " created successfully"));
		}
		
		return CommandResult.success();
	}
}
