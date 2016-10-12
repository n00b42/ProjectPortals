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

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		Help help = new Help("hrename", "rename", " Rename portal");
		help.setPermission("pjp.cmd.home.rename");
		help.setSyntax(" /home rename <oldName> <newName>\n /h rn <oldName> <newName>");
		help.setExample(" /home rename MyPortal ThisPortal");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
		}
		Player player = (Player) src;
		
		String oldName = args.<String> getOne("oldName").get().toLowerCase();

		Map<String, Home> homeList = new HashMap<>();

		Optional<Map<String, Home>> optionalHomeList = player.get(Keys.HOMES);

		if (optionalHomeList.isPresent()) {
			homeList = optionalHomeList.get();
		} else {
			player.offer(new HomeData(new HashMap<String, Home>()));
		}

		if (!homeList.containsKey(oldName)) {
			throw new CommandException(Text.of(TextColors.RED, oldName, " does not exist"));
		}
		Home home = homeList.get(oldName);

		String newName = args.<String> getOne("newName").get().toLowerCase();

		if (homeList.containsKey(newName)) {
			throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
		}

		homeList.remove(oldName);
		homeList.put(newName, home);

		DataTransactionResult result = player.offer(new HomeData(homeList));
		if (!result.isSuccessful()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not rename ", oldName), false);
		} else {
			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home renamed to ", newName));
		}
		
		return CommandResult.success();
	}

}
