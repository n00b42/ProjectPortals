package com.gmail.trentech.pjp.commands.warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.portals.Warp;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "warp").getString();
		
		Help help = new Help("wlist", "list", " List all warp points");
		help.setSyntax(" /warp list\n /" + alias + " l");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Warps")).build());

		List<Text> list = new ArrayList<>();
		
		List<Warp> warps = Warp.list();

		for(Warp warp : warps){
			if(!player.hasPermission("pjp.warps." + warp.getName())){
				continue;
			}
			
			Optional<Location<World>> optionalLocation = warp.getDestination();
			
			if(!optionalLocation.isPresent()){
				continue;
			}
			Location<World> location = optionalLocation.get();
			
			Builder builder = Text.builder().color(TextColors.AQUA).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to remove warp point")));
			builder.onClick(TextActions.runCommand("/warp remove " + warp.getName())).append(Text.of(TextColors.AQUA, warp.getName(), ": ", TextColors.GREEN,
					location.getExtent().getName(),", ", location.getBlockX(), ", ", location.getBlockY(), ", ", location.getBlockZ()));
			
			list.add(builder.build());
		}

		if(list.isEmpty()){
			list.add(Text.of(TextColors.YELLOW, " No warp points"));
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
