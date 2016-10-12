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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.data.portal.Portal;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.listeners.LegacyListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.portal.LegacyBuilder;
import com.gmail.trentech.pjp.portal.PortalProperties;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

import flavor.pie.spongycord.SpongyCord;

public class CMDCreate implements CommandExecutor {

	public CMDCreate() {
		Help help = new Help("pcreate", "create", " Use this command to create a portal that will teleport you to other worlds");
		help.setPermission("pjp.cmd.portal.create");
		help.setSyntax(" /portal create <name> <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-e <particle[:color]>] [-p <price>]\n /p <name> <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-e <particle[:color]>] [-p <price>]");
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

		String destination = args.<String> getOne("destination").get();

		Optional<World> world = Optional.empty();
		Optional<Location<World>> location = Optional.empty();
		AtomicReference<Rotation> rotation = new AtomicReference<>(Rotation.EAST);
		AtomicReference<Double> price = new AtomicReference<>(0.0);
		AtomicReference<Particle> particle = new AtomicReference<>(Particles.getDefaultEffect("portal"));
		AtomicReference<Optional<ParticleColor>> color = new AtomicReference<>(Particles.getDefaultColor("portal", particle.get().isColorable()));
		
		if (args.hasAny("price")) {
			price.set(args.<Double> getOne("price").get());
		}

		if (args.hasAny("particle")) {
			particle.set(args.<Particles> getOne("particle").get().getParticle());

			if (args.hasAny("color")) {
				color.set(Optional.of(args.<ParticleColor> getOne("color").get()));
			}
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

					if (ConfigManager.get().getConfig().getNode("options", "portal", "legacy_builder").getBoolean()) {
						LegacyListener.builders.put(player.getUniqueId(), new LegacyBuilder(name, Optional.of(destination), Optional.empty(), Optional.empty(), rotation.get(), particle.get(), color.get(), price.get()));
						player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by ")).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
					} else {
						PortalListener.props.put(player.getUniqueId(), new PortalProperties(name, Optional.of(destination), Optional.empty(), Optional.empty(), rotation.get(), particle.get(), color.get(), price.get()));
						player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click bottom with empty hand similar to vanilla nether portals "));
					}

					player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by ")).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
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

			if (args.hasAny("rotation")) {
				rotation.set(args.<Rotation> getOne("rotation").get());
			}

			if (ConfigManager.get().getConfig().getNode("options", "portal", "legacy_builder").getBoolean()) {
				LegacyListener.builders.put(player.getUniqueId(), new LegacyBuilder(name, Optional.empty(), world, location, rotation.get(), particle.get(), color.get(), price.get()));
				player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by ")).onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
			} else {
				PortalListener.props.put(player.getUniqueId(), new PortalProperties(name, Optional.empty(), world, location, rotation.get(), particle.get(), color.get(), price.get()));
				player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click bottom with empty hand similar to vanilla nether portals "));
			}
		}

		return CommandResult.success();
	}

}
