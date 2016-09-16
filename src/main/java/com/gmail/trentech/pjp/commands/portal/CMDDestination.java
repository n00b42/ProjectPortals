package com.gmail.trentech.pjp.commands.portal;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.data.object.Portal;
import com.gmail.trentech.pjp.utils.Help;

import flavor.pie.spongycord.SpongyCord;

public class CMDDestination implements CommandExecutor {

	public CMDDestination() {
		Help help = new Help("destination", "destination", " change a portals destination");
		help.setPermission("pjp.cmd.portal.destination");
		help.setSyntax(" /portal destination <name> <destination> [x,y,z]\n /p d <name> <destination> [x,y,z]");
		help.setExample(" /portal destination MyPortal DIM1\n /portal destination Skyland 100,65,400");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;
		
		String name = args.<String> getOne("name").get().toLowerCase();

		if (!Portal.get(name).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}
		Portal portal = Portal.get(name).get();

		AtomicReference<String> destination = new AtomicReference<>(args.<String> getOne("destination").get());

		if (portal.isBungee()) {
			Consumer<List<String>> consumer1 = (list) -> {
				if (!list.contains(destination.get())) {
					try {
						throw new CommandException(Text.of(TextColors.RED, destination.get(), " does not exist"), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Consumer<String> consumer2 = (s) -> {
					if (destination.get().equalsIgnoreCase(s)) {
						try {
							throw new CommandException(Text.of(TextColors.RED, "Destination cannot be the server you are currently on"), false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};

				SpongyCord.API.getServerName(consumer2, player);
			};

			SpongyCord.API.getServerList(consumer1, player);
		} else {
			if (!Sponge.getServer().getWorld(destination.get()).isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, destination.get(), " is not loaded or does not exist"), false);
			}

			destination.set(destination.get() + ":spawn");

			if (args.hasAny("x,y,z")) {
				String[] coords = args.<String> getOne("x,y,z").get().split(",");

				if (coords[0].equalsIgnoreCase("random")) {
					destination.set(destination.get().replace("spawn", "random"));
				} else {
					int x;
					int y;
					int z;

					try {
						x = Integer.parseInt(coords[0]);
						y = Integer.parseInt(coords[1]);
						z = Integer.parseInt(coords[2]);
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
					}
					destination.set(destination.get().replace("spawn", x + "." + y + "." + z));
				}
			}
		}
		
		portal.setDestination(destination.get());
		portal.update();

		return CommandResult.success();
	}

}
