package com.gmail.trentech.pjp.portal.features;

import static com.gmail.trentech.pjp.data.DataQueries.WORLD;
import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.RANDOM;
import static com.gmail.trentech.pjp.data.DataQueries.BED_RESPAWN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.TeleportManager;
import com.gmail.trentech.pjp.Main;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class Coordinate implements DataSerializable {

	private World world;
	private Optional<Vector3d> vector3d = Optional.empty();
	private boolean random = false;
	private boolean bedSpawn = false;

	public Coordinate(World world, Vector3d vector3d) {
		this.world = world;
		this.vector3d = Optional.of(vector3d);
	}
	
	public Coordinate(World world, boolean random, boolean bedSpawn) {
		this.world = world;		
		this.random = random;
		this.bedSpawn = bedSpawn;
	}
	
	public Coordinate(Location<World> location) {
		this.world = location.getExtent();
		this.vector3d = Optional.of(location.getPosition());
	}
	
	public World getWorld() {
		return world;
	}

	public boolean isRandom() {
		return random;
	}

	public boolean isBedSpawn() {
		return bedSpawn;
	}

	public Optional<Location<World>> getLocation() {
		if (random) {
			return TeleportManager.getRandomLocation(world, ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "random_spawn_radius").getInt());
		} else if(bedSpawn) {
			return Optional.of(world.getSpawnLocation());
		} else {
			if (vector3d.isPresent()) {
				return Optional.of(new Location<World>(world, this.vector3d.get()));
			} else {
				return Optional.of(world.getSpawnLocation());
			}
		}
	}
	
	@Override
	public int getContentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer dataContainer = DataContainer.createNew().set(WORLD, world.getName()).set(RANDOM, random).set(BED_RESPAWN, bedSpawn);
		
		if(vector3d.isPresent()) {
			dataContainer.set(VECTOR3D, DataTranslators.VECTOR_3_D.translate(vector3d.get()));
		}
		
		return dataContainer;
	}

	public static class Builder extends AbstractDataBuilder<Coordinate> {

		public Builder() {
			super(Coordinate.class, 0);
		}

		@Override
		protected Optional<Coordinate> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(WORLD)) {
				Optional<World> optionalWorld = Sponge.getServer().getWorld(container.getString(WORLD).get());

				if (!optionalWorld.isPresent()) {
					return Optional.empty();
				}
				World world = optionalWorld.get();
				
				if (container.contains(VECTOR3D)) {
					Vector3d vector3d = DataTranslators.VECTOR_3_D.translate(container.getView(VECTOR3D).get());
					
					return Optional.of(new Coordinate(world, vector3d));
				}
				
				boolean random = container.getBoolean(RANDOM).get();
				boolean bedSpawn = container.getBoolean(BED_RESPAWN).get();
				
				return Optional.of(new Coordinate(world, random, bedSpawn));
				
			}

			return Optional.empty();
		}
	}

	public static String serialize(Location<World> location) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			node.setValue(TypeToken.of(Coordinate.class), new Coordinate(location));		
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Optional<Location<World>> deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();

			return loader.load().getValue(TypeToken.of(Coordinate.class)).getLocation();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
