package com.gmail.trentech.pjp.data.builder.data;

import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;
import static com.gmail.trentech.pjp.data.DataQueries.SERVER;
import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.WORLD;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.portal.Door;
import com.gmail.trentech.pjp.rotation.Rotation;

public class DoorBuilder extends AbstractDataBuilder<Door> {

	public DoorBuilder() {
		super(Door.class, 1);
	}

	@Override
	protected Optional<Door> buildContent(DataView container) throws InvalidDataException {
		if (container.contains(ROTATION, PRICE)) {
			Optional<String> server = Optional.empty();
			Optional<World> world = Optional.empty();
			Optional<Location<World>> location = Optional.empty();
			Rotation rotation = Rotation.get(container.getString(ROTATION).get()).get();
			Double price = container.getDouble(PRICE).get();
			
			if(container.contains(SERVER)) {
				server = Optional.of(container.getString(SERVER).get());
				
				return Optional.of(new Door(server, world, location, rotation, price));
			}

			if(container.contains(WORLD)) {
				String worldName = container.getString(WORLD).get();
				
				Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);
				
				if(optionalWorld.isPresent()) {
					world = optionalWorld;
				} else {
					Main.instance().getLog().error("DataQueries.WORLD is invalid");
					return Optional.empty();
				}
			} else {
				Main.instance().getLog().error("DataQueries.WORLD does not exist");
				return Optional.empty();
			}
			
			if(container.contains(VECTOR3D)) {
				location = Optional.of(new Location<World>(world.get(), DataTranslators.VECTOR_3_D.translate(container.getView(VECTOR3D).get())));
			}
			
			return Optional.of(new Door(server, world, location, rotation, price));
		}

		return Optional.empty();
	}
}
