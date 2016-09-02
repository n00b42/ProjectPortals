package com.gmail.trentech.pjp.commands;

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

import com.gmail.trentech.pjp.utils.Rotation;

import flavor.pie.spongycord.SpongyCord;

public abstract class CMDObjBase implements CommandExecutor {

	String name;

	public CMDObjBase(String name) {
		this.name = name;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		AtomicReference<String> destination = new AtomicReference<>(args.<String> getOne("destination").get());

		AtomicReference<Double> price = new AtomicReference<>(0.0);

		if (args.hasAny("price")) {
			price.set(args.<Double> getOne("price").get());
		}

		AtomicReference<Rotation> rotation = new AtomicReference<>(Rotation.EAST);
		final boolean isBungee = args.hasAny("b");

		if (isBungee) {
			Consumer<List<String>> consumer1 = (list) -> {
				if (!list.contains(destination.get())) {
					player.sendMessage(Text.of(TextColors.DARK_RED, destination.get(), " does not exist"));
					return;
				}

				Consumer<String> consumer2 = (s) -> {
					if (destination.get().equalsIgnoreCase(s)) {
						player.sendMessage(Text.of(TextColors.DARK_RED, "Destination cannot be the server you are currently on"));
						return;
					}

					init(player, destination.get(), rotation.get(), price.get(), isBungee);

					player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place " + name + " to create " + name + " portal"));
				};

				SpongyCord.API.getServerName(consumer2, player);
			};

			SpongyCord.API.getServerList(consumer1, player);
		} else {
			if (!Sponge.getServer().getWorld(destination.get()).isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, destination, " is not loaded or does not exist"));
				return CommandResult.empty();
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
						src.sendMessage(Text.of(TextColors.RED, "Incorrect coordinates"));
						return CommandResult.empty();
					}
					destination.set(destination.get().replace("spawn", x + "." + y + "." + z));
				}
			}

			if (args.hasAny("direction")) {
				rotation.set(args.<Rotation> getOne("direction").get());
			}

			init(player, destination.get(), rotation.get(), price.get(), isBungee);

			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place " + name + " to create " + name + " portal"));
		}

		return CommandResult.success();
	}

	protected abstract void init(Player player, String destination, Rotation rotation, Double price, boolean isBungee);
}
