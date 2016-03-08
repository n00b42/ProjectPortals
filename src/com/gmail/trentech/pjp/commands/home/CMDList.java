package com.gmail.trentech.pjp.commands.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "home").getString();
		
		Help help = new Help("hlist", "list", " List all homes");
		help.setSyntax(" /home list\n /" + alias + " l");
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
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Homes")).build());
		
		HomeData homeData;

		Optional<HomeData> optionalHomeData = player.get(HomeData.class);
		
		if(optionalHomeData.isPresent()){
			homeData = optionalHomeData.get();
		}else{
			homeData = new HomeData();
		}
		
		List<Text> list = new ArrayList<>();

		for(Entry<String, String> home : homeData.homes().get().entrySet()){
			String homeName = home.getKey().toString();
			home.getValue().split(":");
			
			String[] destination = home.getValue().split(":");
			
			String worldName = destination[0];

			String[] coords = destination[1].split("\\.");
			
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);

			Builder builder = Text.builder().color(TextColors.AQUA).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to remove home")));
			builder.onClick(TextActions.runCommand("/home remove " + homeName)).append(Text.of(TextColors.AQUA, homeName, ": ", TextColors.GREEN, worldName,", ", x, ", ", y, ", ", z));
			list.add(builder.build());
		}

		if(list.isEmpty()){
			list.add(Text.of(TextColors.YELLOW, " No saved homes"));
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
