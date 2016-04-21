package com.gmail.trentech.pjp.commands.portal;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDPrice implements CommandExecutor {

	public CMDPrice(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "portal").getString();
		
		Help help = new Help("price", "price", " Charge players for using portals. 0 to disable");
		help.setSyntax(" /portal price <name> <price>\n /" + alias + " pr <name> <price>");
		help.setExample(" /portal price MyPortal 50\n /portal price MyPortal 0");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}

		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get();

		if(!Portal.getByName(name).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " does not exist"));
			return CommandResult.empty();
		}	
		Portal portal = Portal.getByName(name).get();
		
		if(!args.hasAny("price")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		
		double price;
		try{
			price = Double.parseDouble(args.<String>getOne("price").get());
		}catch(Exception e){
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		portal.setPrice(price);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set price of portal ", portal.getName(), " to $", price));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.YELLOW, "/portal price <name> ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter a number amount or 0 to disable"))).append(Text.of("<price>")).build();
		return Text.of(t1,t2);
	}
}
