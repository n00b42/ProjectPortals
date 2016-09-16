package com.gmail.trentech.pjp.commands.portal;

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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.pjp.data.object.Portal;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.listeners.LegacyListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.portal.LegacyBuilder;
import com.gmail.trentech.pjp.portal.PortalProperties;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Rotation;

import flavor.pie.spongycord.SpongyCord;

public class CMDCreate implements CommandExecutor {

	public CMDCreate() {
		Help help = new Help("pcreate", "create", " Use this command to create a portal that will teleport you to other worlds");
		help.setPermission("pjp.cmd.portal.create");
		help.setSyntax(" /portal create <name> <destination> [-b] [-c <x,y,z>] [-d <direction>] [-e <particle[:color]>] [-p <price>]\n /p <name> <destination> [-b] [-c <x,y,z>] [-d <direction>] [-e <particle[:color]>] [-p <price>]");
		help.setExample(" /portal create MyPortal MyWorld\n /portal create MyPortal MyWorld -c -100,65,254\n /portal create MyPortal MyWorld -c random\n /portal create MyPortal MyWorld -c -100,65,254 -d south\n /portal create MyPortal MyWorld -d southeast\n /portal create MyPortal MyWorld -p 50\n /portal create MyPortal MyWorld -e REDSTONE:BLUE");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		String name = args.<String> getOne("name").get().toLowerCase();

		if (Portal.get(name).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " already exists"), false);
		}

		AtomicReference<String> destination = new AtomicReference<>(args.<String> getOne("destination").get());

		AtomicReference<Double> price = new AtomicReference<>(0.0);

		if (args.hasAny("price")) {
			price.set(args.<Double> getOne("price").get());
		}

		AtomicReference<Particle> particle = new AtomicReference<>(Particles.getDefaultEffect("portal"));

		AtomicReference<Optional<ParticleColor>> color = new AtomicReference<>(Particles.getDefaultColor("portal", particle.get().isColorable()));

		if (args.hasAny("particle")) {
			particle.set(args.<Particles> getOne("particle").get().getParticle());

			if (args.hasAny("color")) {
				color.set(Optional.of(args.<ParticleColor> getOne("color").get()));
			}
		}

		AtomicReference<Rotation> rotation = new AtomicReference<>(Rotation.EAST);
		final boolean isBungee = args.hasAny("b");

		if (isBungee) {
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

					if (ConfigManager.get().getConfig().getNode("options", "portal", "legacy_builder").getBoolean()) {
						LegacyListener.builders.put(player.getUniqueId(), new LegacyBuilder(name, destination.get(), rotation.get(), particle.get(), color.get(), price.get(), isBungee));
						player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by ")).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
					} else {
						PortalListener.props.put(player.getUniqueId(), new PortalProperties(name, destination.get(), rotation.get(), particle.get(), color.get(), price.get(), isBungee));
						player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click bottom with empty hand similar to vanilla nether portals "));
					}

					player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by ")).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
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

			if (args.hasAny("direction")) {
				rotation.set(args.<Rotation> getOne("direction").get());
			}

			if (ConfigManager.get().getConfig().getNode("options", "portal", "legacy_builder").getBoolean()) {
				LegacyListener.builders.put(player.getUniqueId(), new LegacyBuilder(name, destination.get(), rotation.get(), particle.get(), color.get(), price.get(), isBungee));
				player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by ")).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
			} else {
				PortalListener.props.put(player.getUniqueId(), new PortalProperties(name, destination.get(), rotation.get(), particle.get(), color.get(), price.get(), isBungee));
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click bottom with empty hand similar to vanilla nether portals "));
			}
		}

		return CommandResult.success();
	}

}
