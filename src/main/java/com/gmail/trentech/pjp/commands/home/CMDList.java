package com.gmail.trentech.pjp.commands.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.portal.Home;
import com.gmail.trentech.pjp.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList() {
		Help help = new Help("hlist", "list", " List all homes");
		help.setPermission("pjp.cmd.home.list");
		help.setSyntax(" /home list\n /h l");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		Map<String, Home> homeList = new HashMap<>();

		Optional<Map<String, Home>> optionalHomeList = player.get(Keys.HOMES);

		if (optionalHomeList.isPresent()) {
			homeList = optionalHomeList.get();
		} else {
			player.offer(new HomeData(new HashMap<String, Home>()));
		}

		List<Text> list = new ArrayList<>();

		for (Entry<String, Home> entry : homeList.entrySet()) {
			String homeName = entry.getKey().toString();
			Home home = entry.getValue();

			Builder builder = Text.builder().onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to teleport to home")));

			Optional<Location<World>> optionalDestination = home.getLocation();
			
			if (optionalDestination.isPresent()) {
				Location<World> destination = optionalDestination.get();

				String worldName = destination.getExtent().getName();
				int x = destination.getBlockX();
				int y = destination.getBlockY();
				int z = destination.getBlockZ();

				builder.onClick(TextActions.runCommand("/home " + homeName)).append(Text.of(TextColors.GREEN, homeName, ": ", TextColors.WHITE, worldName, ", ", x, ", ", y, ", ", z));
			} else {
				builder.onClick(TextActions.runCommand("/home " + homeName)).append(Text.of(TextColors.GREEN, homeName, ": ", TextColors.RED, "INVALID DESTINATION"));
			}

			list.add(builder.build());
		}

		if (list.isEmpty()) {
			list.add(Text.of(TextColors.YELLOW, " No saved homes"));
		}

		PaginationList.Builder pages = PaginationList.builder();

		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Homes")).build());

		pages.contents(list);

		pages.sendTo(src);

		return CommandResult.success();
	}

}
