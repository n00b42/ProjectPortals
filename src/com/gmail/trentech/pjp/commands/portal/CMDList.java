package com.gmail.trentech.pjp.commands.portal;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "portal").getString();
		
		Help help = new Help("plist", "list", " List all portals");
		help.setSyntax(" /portal list\n /" + alias + " l");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Portals")).build());
		
		List<Text> list = new ArrayList<>();
		
		List<Portal> portals = Portal.list();

		for(Portal portal : portals){
			Location<World> location = portal.getFrame().get(0);
			String worldName = location.getExtent().getName();
			list.add(Text.of(TextColors.AQUA, "Name: ", TextColors.GREEN, portal.getName(), TextColors.AQUA, " Location: ", worldName, " ", location.getBlockX(), " ", location.getBlockY(), " ", location.getBlockZ()));
		}

		if(list.isEmpty()){
			list.add(Text.of(TextColors.YELLOW, " No portals"));
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
