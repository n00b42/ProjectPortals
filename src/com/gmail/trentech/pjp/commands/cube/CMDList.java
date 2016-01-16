package com.gmail.trentech.pjp.commands.cube;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.portals.Cuboid;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "cube").getString();
		
		Help help = new Help("clist", "list", " List all cubes");
		help.setSyntax(" /cube list\n /" + alias + " l");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of(TextColors.RED, "[DEPRECATED] ", TextColors.YELLOW, "/cube command will be removed in a future build in favor of /portal command"));
		
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Cube Portals")).build());
		
		List<Text> list = new ArrayList<>();
		
		List<Cuboid> cubes = Cuboid.list();

		for(Cuboid cube : cubes){
			Location<World> location = cube.getRegion().get(0);
			String worldName = location.getExtent().getName();
			list.add(Text.of(TextColors.AQUA, "Name: ", TextColors.GREEN, cube.getName(), TextColors.AQUA, " Location: ", worldName, " ", location.getBlockX(), " ", location.getBlockY(), " ", location.getBlockZ()));
		}

		if(list.isEmpty()){
			list.add(Text.of(TextColors.YELLOW, " No cubes"));
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
