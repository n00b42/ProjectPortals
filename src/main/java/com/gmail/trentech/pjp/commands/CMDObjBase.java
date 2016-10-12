package com.gmail.trentech.pjp.commands;

import java.util.List;
import java.util.Optional;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.rotation.Rotation;

import flavor.pie.spongycord.SpongyCord;

public abstract class CMDObjBase implements CommandExecutor {

	String name;

	public CMDObjBase(String name) {
		this.name = name;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		String destination = args.<String> getOne("destination").get();

		Optional<World> world = Optional.empty();
		Optional<Location<World>> location = Optional.empty();
		AtomicReference<Rotation> direction = new AtomicReference<>(Rotation.EAST);
		AtomicReference<Double> price = new AtomicReference<>(0.0);

		if (args.hasAny("price")) {
			price.set(args.<Double> getOne("price").get());
		}
		
		final boolean isBungee = args.hasAny("b");

		if (isBungee) {
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

					init(player, Optional.of(destination), Optional.empty(), Optional.empty(), direction.get(), price.get());

					player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place " + name + " to create " + name + " portal"));
				};

				SpongyCord.API.getServerName(consumer2, player);
			};

			SpongyCord.API.getServerList(consumer1, player);
		} else {
			world = Sponge.getServer().getWorld(destination);
			
			if (!world.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, destination, " is not loaded or does not exist"), false);
			}

			if (args.hasAny("x,y,z")) {
				String[] coords = args.<String> getOne("x,y,z").get().split(",");

				if (coords[0].equalsIgnoreCase("random")) {
					location = Optional.of(world.get().getLocation(0, 0, 0));
				} else {
					try {
						location = Optional.of(world.get().getLocation(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
					}		
				}
			}

			if (args.hasAny("direction")) {
				direction.set(args.<Rotation> getOne("direction").get());
			}

			init(player, Optional.of(destination), world, location, direction.get(), price.get());

			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place " + name + " to create " + name + " portal"));
		}

		return CommandResult.success();
	}

	protected abstract void init(Player player, Optional<String> server, Optional<World> world, Optional<Location<World>> location, Rotation rotation, double price);
}
