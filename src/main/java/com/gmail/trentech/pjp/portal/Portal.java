package com.gmail.trentech.pjp.portal;

import static com.gmail.trentech.pjp.data.DataQueries.BED_RESPAWN;
import static com.gmail.trentech.pjp.data.DataQueries.FORCE;
import static com.gmail.trentech.pjp.data.DataQueries.PERMISSION;
import static com.gmail.trentech.pjp.data.DataQueries.PORTAL_TYPE;
import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.PROPERTIES;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;
import static com.gmail.trentech.pjp.data.DataQueries.SERVER;
import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.WORLD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjc.core.BungeeManager;
import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pjc.core.TeleportManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public abstract class Portal implements DataSerializable {

	private final PortalType type;
	private String name;
	private Rotation rotation = Rotation.EAST;
	private double price = 0;
	private Optional<String> permission = Optional.empty();
	
	private Optional<Properties> properties = Optional.empty();

	private static ConcurrentHashMap<String, Portal> cache = new ConcurrentHashMap<>();

	protected Portal(PortalType type, Rotation rotation, double price, Optional<String> permission) {
		this.type = type;
		this.rotation = rotation;
		this.price = price;
		this.permission = permission;
	}

	public static Optional<Portal> get(String name, PortalType type) {
		if (cache.containsKey(name)) {
			Portal portal = cache.get(name);

			if (portal.getType().equals(type)) {
				return Optional.of(cache.get(name));
			}
		}

		return Optional.empty();
	}

	public static Optional<Portal> get(Location<World> location, PortalType type) {
		if (type.equals(PortalType.PORTAL)) {
			for (Entry<String, Portal> entry : cache.entrySet()) {
				Portal portal = entry.getValue();

				if (!portal.getType().equals(type)) {
					continue;
				}

				Properties properties = portal.getProperties().get();

				List<Location<World>> frame = properties.getFrame();

				if (!frame.get(0).getExtent().equals(location.getExtent())) {
					continue;
				}

				for (Location<World> loc : frame) {
					if (loc.getBlockPosition().equals(location.getBlockPosition())) {
						return Optional.of(portal);
					}
				}

				for (Location<World> loc : properties.getFill()) {
					if (loc.getBlockPosition().equals(location.getBlockPosition())) {
						return Optional.of(portal);
					}
				}
			}
		}

		return get(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ(), type);
	}

	public static List<Portal> all(PortalType type) {
		List<Portal> list = new ArrayList<>();

		for (Entry<String, Portal> entry : cache.entrySet()) {
			Portal portal = entry.getValue();

			if (portal.getType().equals(type)) {
				list.add(portal);
			}
		}

		return list;
	}

	public static void init() {
		try {
			String database = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "sql", "database").getString();

			SQLManager sqlManager = SQLManager.get(Main.getPlugin(), database);
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PORTALS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String name = result.getString("Name");

				Portal portal = deserialize(result.getString("Data"));
				portal.setName(name);

				cache.put(name, portal);

				if (portal.getProperties().isPresent()) {
					Properties properties = portal.getProperties().get();
					properties.getParticle().createTask(name, properties.getFill(), properties.getParticleColor());
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void create(String name) {
		this.name = name;

		try {
			String database = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "sql", "database").getString();

			SQLManager sqlManager = SQLManager.get(Main.getPlugin(), database);
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + sqlManager.getPrefix("PORTALS") + " (Name, Data) VALUES (?, ?)");

			statement.setString(1, name);
			statement.setString(2, serialize(this));

			statement.executeUpdate();

			connection.close();

			cache.put(name, this);

			if (properties.isPresent()) {
				Properties properties = this.properties.get();
				properties.getParticle().createTask(name, properties.getFill(), properties.getParticleColor());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void create(Location<World> location) {
		name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		create(name);

		Particle particle = Particles.getDefaultEffect("creation");
		particle.spawnParticle(location, false, Particles.getDefaultColor("creation", particle.isColorable()));
	}

	public void update() {
		try {
			String database = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "sql", "database").getString();

			SQLManager sqlManager = SQLManager.get(Main.getPlugin(), database);
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("PORTALS") + " SET Data = ? WHERE Name = ?");

			statement.setString(1, serialize(this));
			statement.setString(2, name);

			statement.executeUpdate();

			connection.close();

			cache.put(name, this);

			if (properties.isPresent()) {
				Properties properties = this.properties.get();

				for (Task task : Sponge.getScheduler().getScheduledTasks()) {
					if (task.getName().equals(name)) {
						task.cancel();
						break;
					}
				}
				properties.update(true);
				properties.getParticle().createTask(name, properties.getFill(), properties.getParticleColor());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void remove() {
		try {
			String database = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "sql", "database").getString();

			SQLManager sqlManager = SQLManager.get(Main.getPlugin(), database);
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from " + sqlManager.getPrefix("PORTALS") + " WHERE Name = ?");

			statement.setString(1, name);
			statement.executeUpdate();

			connection.close();

			cache.remove(name);

			if (properties.isPresent()) {
				Properties properties = this.properties.get();

				for (Task task : Sponge.getScheduler().getScheduledTasks()) {
					if (task.getName().equals(name)) {
						task.cancel();
						break;
					}
				}
				properties.update(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PortalType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Optional<String> getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = Optional.of(permission);
	}
	
	public Optional<Properties> getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = Optional.of(properties);
	}

	public static class Server extends Portal {

		private String server;

		public Server(PortalType type, String server, Rotation rotation, double price, Optional<String> permission) {
			super(type, rotation, price, permission);
			this.server = server;
		}

		public String getServer() {
			return server;
		}

		public void setServer(String server) {
			this.server = server;
		}

		@Override
		public int getContentVersion() {
			return 0;
		}

		@Override
		public DataContainer toContainer() {
			DataContainer container = new MemoryDataContainer().set(PORTAL_TYPE, getType().name()).set(SERVER, server).set(ROTATION, getRotation().getName()).set(PRICE, getPrice());

			if (getPermission().isPresent()) {
				container.set(PERMISSION, getPermission().get());
			}
			
			if (getProperties().isPresent()) {
				container.set(PROPERTIES, getProperties().get());
			}

			return container;
		}

		public static class Builder extends AbstractDataBuilder<Server> {

			public Builder() {
				super(Server.class, 0);
			}

			@Override
			protected Optional<Server> buildContent(DataView container) throws InvalidDataException {
				if (container.contains(PORTAL_TYPE, SERVER, ROTATION, PRICE)) {
					PortalType type = PortalType.valueOf(container.getString(PORTAL_TYPE).get());
					String server = container.getString(SERVER).get();
					Rotation rotation = Rotation.get(container.getString(ROTATION).get()).get();
					Double price = container.getDouble(PRICE).get();
					Optional<String> permission = container.getString(PERMISSION);

					Portal.Server portal = new Portal.Server(type, server, rotation, price, permission);

					if (container.contains(PROPERTIES)) {
						portal.setProperties(container.getSerializable(PROPERTIES, Properties.class).get());
					}

					return Optional.of(portal);
				}

				return Optional.empty();
			}
		}
	}

	public static class Local extends Portal {

		private World world;
		private Optional<Vector3d> vector3d;
		private boolean bedSpawn;
		private boolean force;
		
		public Local(PortalType type, World world, Optional<Vector3d> vector3d, Rotation rotation, double price, boolean bedSpawn, boolean force, Optional<String> permission) {
			super(type, rotation, price, permission);
			this.world = world;
			this.vector3d = vector3d;
			this.bedSpawn = bedSpawn;
			this.force = force;
		}

		public World getWorld() {
			return world;
		}

		public void setWorld(World world) {
			this.world = world;
		}

		public Optional<Vector3d> getVector3d() {
			return vector3d;
		}

		public void setVector3d(Vector3d vector3d) {
			this.vector3d = Optional.of(vector3d);
		}

		public boolean force() {
			return force;
		}
		
		public void setSet(boolean force) {
			this.force = force;
		}
		
		public boolean isBedSpawn() {
			return bedSpawn;
		}
		
		public void setBedSpawn(boolean bedSpawn) {
			this.bedSpawn = bedSpawn;
		}
		
		public Optional<Location<World>> getLocation() {
			if (vector3d.isPresent()) {
				Vector3d vector3d = this.vector3d.get();

				if (vector3d.getX() == 0 && vector3d.getY() == 0 && vector3d.getZ() == 0) {
					return TeleportManager.getRandomLocation(world, ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "random_spawn_radius").getInt());
				} else {
					return Optional.of(new Location<World>(world, vector3d));
				}
			} else {
				return Optional.of(world.getSpawnLocation());
			}
		}

		
		@Override
		public int getContentVersion() {
			return 0;
		}

		@Override
		public DataContainer toContainer() {
			DataContainer container = new MemoryDataContainer().set(PORTAL_TYPE, getType().name()).set(WORLD, world.getName()).set(ROTATION, getRotation().getName()).set(PRICE, getPrice()).set(BED_RESPAWN, isBedSpawn()).set(FORCE, force());

			if (getPermission().isPresent()) {
				container.set(PERMISSION, getPermission().get());
			}
			
			if (getProperties().isPresent()) {
				container.set(PROPERTIES, getProperties().get());
			}

			if (vector3d.isPresent()) {
				Vector3d vector3d = this.vector3d.get();
				container.set(VECTOR3D, DataTranslators.VECTOR_3_D.translate(vector3d));
			}

			return container;
		}

		public static class Builder extends AbstractDataBuilder<Local> {

			public Builder() {
				super(Local.class, 0);
			}

			@Override
			protected Optional<Local> buildContent(DataView container) throws InvalidDataException {
				if (container.contains(PORTAL_TYPE, WORLD, ROTATION, PRICE)) {
					Optional<World> optionalWorld = Sponge.getServer().getWorld(container.getString(WORLD).get());

					if (!optionalWorld.isPresent()) {
						return Optional.empty();
					}

					PortalType type = PortalType.valueOf(container.getString(PORTAL_TYPE).get());
					World world = optionalWorld.get();
					Optional<Vector3d> vector3d = Optional.empty();
					Rotation rotation = Rotation.get(container.getString(ROTATION).get()).get();
					Double price = container.getDouble(PRICE).get();
					boolean bedRespawn = false;
					boolean force = false;
					Optional<String> permission = container.getString(PERMISSION);
					
					if(container.contains(BED_RESPAWN)) {
						bedRespawn = container.getBoolean(BED_RESPAWN).get();
					}
					
					if(container.contains(FORCE)) {
						force = container.getBoolean(FORCE).get();
					}
					
					if (container.contains(VECTOR3D)) {
						vector3d = Optional.of(DataTranslators.VECTOR_3_D.translate(container.getView(VECTOR3D).get()));
					}

					Portal.Local portal = new Portal.Local(type, world, vector3d, rotation, price, bedRespawn, force, permission);

					if (container.contains(PROPERTIES)) {
						portal.setProperties(container.getSerializable(PROPERTIES, Properties.class).get());
					}

					return Optional.of(portal);
				}

				return Optional.empty();
			}
		}
	}

	public enum PortalType {
		BUTTON,
		DOOR,
		HOME,
		LEVER,
		PLATE,
		PORTAL,
		SIGN,
		WARP;
	}
	
	public static String serialize(Portal portal) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			if (portal instanceof Server) {
				node.setValue(TypeToken.of(Server.class), ((Server) portal));
			} else {
				node.setValue(TypeToken.of(Local.class), ((Local) portal));
			}
			
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Portal deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
			ConfigurationNode node = loader.load();
			try{
				return node.getValue(TypeToken.of(Local.class));
			} catch(Exception e) {
				return node.getValue(TypeToken.of(Server.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean teleportPlayer(Player player, Portal portal) {
		AtomicReference<Boolean> bool = new AtomicReference<>(false);

		if (portal instanceof Portal.Server) {
			Portal.Server server = (Portal.Server) portal;

			Consumer<String> consumer = (serverName) -> {
				TeleportEvent.Server teleportEvent = new TeleportEvent.Server(player, serverName, server.getServer(), server.getPrice(), server.getPermission(), Cause.of(NamedCause.source(server)));

				if (!Sponge.getEventManager().post(teleportEvent)) {
					BungeeManager.connect(player, teleportEvent.getDestination());
					player.setLocation(player.getWorld().getSpawnLocation());

					bool.set(true);
				}
			};
			BungeeManager.getServer(consumer, player);
		} else {
			Portal.Local local = (Portal.Local) portal;

			if(local.isBedSpawn()) {
				Optional<Map<UUID, RespawnLocation>> optionalLocations = player.get(Keys.RESPAWN_LOCATIONS);
				
				if(optionalLocations.isPresent()) {
					Map<UUID, RespawnLocation> respawnLocations = optionalLocations.get();

					if(respawnLocations.containsKey(local.getWorld().getUniqueId())) {
						Optional<Location<World>> optionalLocation = respawnLocations.get(local.getWorld().getUniqueId()).asLocation();
						
						if(optionalLocation.isPresent()) {
							Location<World> spawnLocation = optionalLocation.get();
							
							com.gmail.trentech.pjp.events.TeleportEvent.Local teleportEvent = new TeleportEvent.Local(player, player.getLocation(), spawnLocation, local.getPrice(), local.force(), local.getPermission(), Cause.of(NamedCause.source(local)));

							if (!Sponge.getEventManager().post(teleportEvent)) {
								spawnLocation = teleportEvent.getDestination();

								Vector3d rotation = local.getRotation().toVector3d();

								player.setLocationAndRotation(spawnLocation, rotation);

								return true;
							}
						}
					}
				}
			}
			
			Optional<Location<World>> optionalSpawnLocation = local.getLocation();

			if (optionalSpawnLocation.isPresent()) {
				Location<World> spawnLocation = optionalSpawnLocation.get();

				com.gmail.trentech.pjp.events.TeleportEvent.Local teleportEvent = new TeleportEvent.Local(player, player.getLocation(), spawnLocation, local.getPrice(), local.force(), local.getPermission(), Cause.of(NamedCause.source(local)));

				if (!Sponge.getEventManager().post(teleportEvent)) {
					spawnLocation = teleportEvent.getDestination();

					Vector3d rotation = local.getRotation().toVector3d();

					player.setLocationAndRotation(spawnLocation, rotation);

					bool.set(true);
				}
			} else {
				player.sendMessage(Text.of(TextColors.RED, "Could not find location"));
			}
		}

		return bool.get();
	}
}
