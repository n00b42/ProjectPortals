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
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.portals.Warp;
import com.gmail.trentech.pjp.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList() {
		Help help = new Help("wlist", "list", " List all warp points");
		help.setSyntax(" /warp list\n /w l");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Warps")).build());

		List<Text> list = new ArrayList<>();
		
		List<Warp> warps = Warp.list();

		for(Warp warp : warps) {
			if(!player.hasPermission("pjp.warps." + warp.getName())) {
				continue;
			}
			
			Optional<Location<World>> optionalLocation = warp.getDestination();
			
			if(!optionalLocation.isPresent()) {
				continue;
			}

			double price = warp.getPrice();
			if(price == 0) {
				list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, warp.getName()));
			}else{
				list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, warp.getName(), TextColors.GREEN, " Price: ", TextColors.WHITE, "$", warp.getPrice()));
			}

		}

		if(list.isEmpty()) {
			list.add(Text.of(TextColors.YELLOW, " No warp points"));
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
