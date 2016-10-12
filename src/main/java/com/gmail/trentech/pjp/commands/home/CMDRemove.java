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

import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.portal.Home;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("hremove", "remove", "Remove an existing home");
		help.setPermission("pjp.cmd.home.remove");
		help.setSyntax(" /home remove <name>\n /h r <name>");
		help.setExample(" /home remove OldHome");
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

		if (!homeList.containsKey(homeName)) {
			throw new CommandException(Text.of(TextColors.RED, homeName, " does not exist"), false);
		}

		homeList.remove(homeName);

		DataTransactionResult result = player.offer(new HomeData(homeList));
		if (!result.isSuccessful()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not remove ", homeName), false);
		} else {
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", homeName, " removed"));
		}

		return CommandResult.success();
	}
}
