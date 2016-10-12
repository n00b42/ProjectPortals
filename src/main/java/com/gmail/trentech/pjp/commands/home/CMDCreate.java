package com.gmail.trentech.pjp.commands.home;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.portal.Home;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDCreate implements CommandExecutor {

	public CMDCreate() {
		Help help = new Help("hcreate", "create", " Create a new home");
		help.setPermission("pjp.cmd.home.create");
		help.setSyntax(" /home create <name>\n /h c <name>");
		help.setExample(" /home create MyHome");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		String homeName = args.<String> getOne("name").get().toLowerCase();

		Map<String, Home> homeList = new HashMap<>();

		Optional<Map<String, Home>> optionalHomeList = player.get(Keys.HOMES);

		if (optionalHomeList.isPresent()) {
			homeList = optionalHomeList.get();
		}

		int defaultAmount = ConfigManager.get().getConfig().getNode("options", "homes").getInt();

		int amount = homeList.size();

		int extra = 0;
		for (int i = 1; i <= 100; i++) {
			if (player.hasPermission("pjp.homes." + i)) {
				extra = i;
				break;
			}
		}

		if (!player.hasPermission("pjp.homes.unlimited")) {
			if (amount >= (defaultAmount + extra)) {
				throw new CommandException(Text.of(TextColors.RED, "You have reached the maximum number of homes you can have"), false);
			}
			amount++;
		}

		if (homeList.containsKey(homeName)) {
			throw new CommandException(Text.of(TextColors.RED, homeName, " already exists."), false);
		}

		Location<World> location = player.getLocation();

		homeList.put(homeName, new Home(location, Rotation.getClosest(player.getRotation().getFloorY())));

		DataTransactionResult result = player.offer(new HomeData(homeList));
		if (!result.isSuccessful()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not create ", homeName), false);
		} else {
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", homeName, " create"));
		}

		return CommandResult.success();
	}
}
