package com.gmail.trentech.pjp.commands.portal;

import java.util.List;
import java.util.Optional;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.portal.Portal;
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

		String destination = args.<String> getOne("destination").get();

		if (portal.getServer().isPresent()) {
			Consumer<List<String>> consumer1 = (list) -> {
				if (!list.contains(destination)) {
					try {
						throw new CommandException(Text.of(TextColors.RED, destination, " does not exist"), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Consumer<String> consumer2 = (s) -> {
					if (destination.equalsIgnoreCase(s)) {
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
			
			portal.setServer(destination);
		} else {
			Optional<World> world = Sponge.getServer().getWorld(destination);

			if (!world.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, destination, " is not loaded or does not exist"), false);
			}

			portal.setWorld(world.get());
			
			if (args.hasAny("x,y,z")) {
				Location<World> location;
				
				String[] coords = args.<String> getOne("x,y,z").get().split(",");

				if (coords[0].equalsIgnoreCase("random")) {
					location = world.get().getLocation(0, 0, 0);
				} else {
					try {
						location = world.get().getLocation(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
					}
				}
				
				portal.setLocation(location);
			}
		}

		portal.update();
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal destination updated"));
		
		return CommandResult.success();
	}

}
