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
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.Server;
import com.gmail.trentech.pjp.utils.Help;

import flavor.pie.spongycord.SpongyCord;

public class CMDDestination implements CommandExecutor {

	public CMDDestination() {
		Help help = new Help("portal destination", "destination", " change as existing portals destination", false);
		help.setPermission("pjp.cmd.portal.destination");
		help.setSyntax(" /portal destination <name> <destination> [x,y,z]\n /p d <name> <destination> [x,y,z]");
		help.setExample(" /portal destination MyPortal DIM1\n /portal destination Skyland 100,65,400\n /portal destination Server1");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		Portal portal = args.<Portal>getOne("name").get();

		String destination = args.<String>getOne("destination").get();

		if (portal instanceof Portal.Server) {
			Portal.Server server = (Server) portal;

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

			server.setServer(destination);
		} else {
			Portal.Local local = (Portal.Local) portal;

			Optional<World> world = Sponge.getServer().getWorld(destination);

			if (!world.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, destination, " is not loaded or does not exist"), false);
			}

			local.setWorld(world.get());

			if (args.hasAny("x,y,z")) {
				Vector3d vector3d;

				String[] coords = args.<String>getOne("x,y,z").get().split(",");

				if (coords[0].equalsIgnoreCase("random")) {
					vector3d = new Vector3d(0, 0, 0);
				} else {
					try {
						vector3d = new Vector3d(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
					}
				}

				local.setVector3d(vector3d);
			}
		}

		portal.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal destination updated"));

		return CommandResult.success();
	}

}
