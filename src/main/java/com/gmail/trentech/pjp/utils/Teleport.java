package com.gmail.trentech.pjp.utils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.events.TeleportEvent.Local;
import com.gmail.trentech.pjp.portal.Portal;

import flavor.pie.spongycord.SpongyCord;

public class Teleport {

	private static ConcurrentHashMap<UUID, Vector3d> cache = new ConcurrentHashMap<>();
	private static ThreadLocalRandom random = ThreadLocalRandom.current();

	public static void cacheRandom(World world) {
		Sponge.getScheduler().createTaskBuilder().execute(c -> {
			TeleportHelper teleportHelper = Sponge.getGame().getTeleportHelper();

			Location<World> spawnLocation = world.getSpawnLocation();

			int radius = ConfigManager.get().getConfig().getNode("options", "random_spawn_radius").getInt() / 2;

			for (int i = 0; i < 49; i++) {
				double x = (random.nextDouble() * (radius * 2) - radius) + spawnLocation.getBlockX();
				double y = random.nextDouble(59, 200 + 1);
				double z = (random.nextDouble() * (radius * 2) - radius) + spawnLocation.getBlockZ();

				Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(world.getLocation(x, y, z));

				if (!optionalLocation.isPresent()) {
					continue;
				}
				Location<World> unsafeLocation = optionalLocation.get();

				BlockType blockType = unsafeLocation.getBlockType();

				if (!blockType.equals(BlockTypes.AIR) || !unsafeLocation.getRelative(Direction.UP).getBlockType().equals(BlockTypes.AIR)) {
					continue;
				}

				BlockType floorBlockType = unsafeLocation.getRelative(Direction.DOWN).getBlockType();

				if (floorBlockType.equals(BlockTypes.WATER) || floorBlockType.equals(BlockTypes.LAVA) || floorBlockType.equals(BlockTypes.FLOWING_WATER) || floorBlockType.equals(BlockTypes.FLOWING_LAVA) || floorBlockType.equals(BlockTypes.FIRE)) {
					continue;
				}

				unsafeLocation.getExtent().loadChunk(unsafeLocation.getChunkPosition(), true);

				cache.put(world.getUniqueId(), unsafeLocation.getPosition());
				break;
			}
		}).submit(Main.getPlugin());
	}

	public static Location<World> getRandomLocation(World world) {
		Location<World> location = world.getLocation(cache.get(world.getUniqueId()));
		cacheRandom(world);
		return location;
	}

	public static Consumer<CommandSource> unsafe(Location<World> location) {
		return (CommandSource src) -> {
			Player player = (Player) src;
			player.setLocation(location);
		};
	}

	public static boolean teleport(Player player, Portal portal) {
		AtomicReference<Boolean> bool = new AtomicReference<>(false);

		if (portal instanceof Portal.Server) {
			Portal.Server server = (Portal.Server) portal;

			Consumer<String> consumer = (serverName) -> {
				TeleportEvent.Server teleportEvent = new TeleportEvent.Server(player, serverName, server.getServer(), server.getPrice(), Cause.of(NamedCause.source(server)));

				if (!Sponge.getEventManager().post(teleportEvent)) {
					SpongyCord.API.connectPlayer(player, teleportEvent.getDestination());

					player.setLocation(player.getWorld().getSpawnLocation());

					bool.set(true);
				}
			};

			SpongyCord.API.getServerName(consumer, player);
		} else {
			Portal.Local local = (Portal.Local) portal;

			Optional<Location<World>> optionalSpawnLocation = local.getLocation();

			if (optionalSpawnLocation.isPresent()) {
				Location<World> spawnLocation = optionalSpawnLocation.get();

				Local teleportEvent = new TeleportEvent.Local(player, player.getLocation(), spawnLocation, local.getPrice(), Cause.of(NamedCause.source(local)));

				if (!Sponge.getEventManager().post(teleportEvent)) {
					spawnLocation = teleportEvent.getDestination();

					Vector3d rotation = local.getRotation().toVector3d();

					player.setLocationAndRotation(spawnLocation, rotation);

					bool.set(true);
				}
			}
		}

		return bool.get();
	}
}
