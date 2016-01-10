package com.gmail.trentech.pjp.commands.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDList implements CommandExecutor {

	public CMDList(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "home").getString();
		
		Help help = new Help("hlist", " List all homes");
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
		
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Homes")).build());
		
		List<Text> list = new ArrayList<>();

		ConfigurationNode config = new ConfigManager("Players", player.getUniqueId().toString() + ".conf").getConfig();
		
		Map<Object, ? extends ConfigurationNode> homes = config.getNode("Homes").getChildrenMap();
		for(Entry<Object, ? extends ConfigurationNode> home : homes.entrySet()){
			String homeName = home.getKey().toString();
			String worldName = config.getNode("Homes", homeName, "World").getString();
			int x = config.getNode("Homes", homeName, "X").getInt();
			int y = config.getNode("Homes", homeName, "Y").getInt();
			int z = config.getNode("Homes", homeName, "Z").getInt();

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
