package com.gmail.trentech.pjp.portal;

import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.WORLD;

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
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class LocationSerializable implements DataSerializable {

	private World world;
	private Vector3d vector3d;
	
	protected LocationSerializable(World world, Vector3d vector3d) {
		this.world = world;
		this.vector3d = vector3d;
	}
	
	protected LocationSerializable(Location<World> location) {
		this.world = location.getExtent();
		this.vector3d = location.getPosition();
	}
	
	public Location<World> getLocation() {
		return new Location<>(world, vector3d);
	}
	
	@Override
	public int getContentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return DataContainer.createNew().set(VECTOR3D, DataTranslators.VECTOR_3_D.translate(vector3d)).set(WORLD, world.getName());
	}

	public static class Builder extends AbstractDataBuilder<LocationSerializable> {

		public Builder() {
			super(LocationSerializable.class, 0);
		}

		@Override
		protected Optional<LocationSerializable> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(WORLD, VECTOR3D)) {
				Optional<World> optionalWorld = Sponge.getServer().getWorld(container.getString(WORLD).get());

				if (!optionalWorld.isPresent()) {
					return Optional.empty();
				}
				World world = optionalWorld.get();
				
				Vector3d vector3d = DataTranslators.VECTOR_3_D.translate(container.getView(VECTOR3D).get());

				return Optional.of(new LocationSerializable(world, vector3d));
			}

			return Optional.empty();
		}
	}

	public static String serialize(Location<World> location) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			node.setValue(TypeToken.of(LocationSerializable.class), new LocationSerializable(location));		
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Location<World> deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();

			return loader.load().getValue(TypeToken.of(LocationSerializable.class)).getLocation();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
