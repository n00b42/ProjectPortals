package com.gmail.trentech.pjp.data.builder.data;

import static com.gmail.trentech.pjp.data.DataQueries.COLOR;
import static com.gmail.trentech.pjp.data.DataQueries.FILL;
import static com.gmail.trentech.pjp.data.DataQueries.FRAME;
import static com.gmail.trentech.pjp.data.DataQueries.PARTICLE;
import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;
import static com.gmail.trentech.pjp.data.DataQueries.SERVER;
import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.WORLD;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.portal.Portal;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.rotation.Rotation;

public class PortalBuilder extends AbstractDataBuilder<Portal> {

	public PortalBuilder() {
		super(Portal.class, 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Optional<Portal> buildContent(DataView container) throws InvalidDataException {
		if (container.contains(FRAME, FILL, PARTICLE, COLOR, ROTATION, PRICE)) {
			Optional<String> server = Optional.empty();
			Optional<World> world = Optional.empty();
			Optional<Location<World>> location = Optional.empty();
			Rotation rotation = Rotation.get(container.getString(ROTATION).get()).get();
			Double price = container.getDouble(PRICE).get();
			
			List<Location<World>> frame = new ArrayList<>();

			for (String loc : (List<String>) container.getList(FRAME).get()) {
				String[] args = loc.split(":");

				Optional<World> optional = Sponge.getServer().getWorld(args[0]);

				if (!optional.isPresent()) {
					continue;
				}
				World extent = optional.get();

				String[] coords = args[1].split("\\.");

				frame.add(new Location<World>(extent, Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
			}

			List<Location<World>> fill = new ArrayList<>();

			for (String loc : (List<String>) container.getList(FILL).get()) {
				String[] args = loc.split(":");

				Optional<World> optional = Sponge.getServer().getWorld(args[0]);

				if (!optional.isPresent()) {
					continue;
				}
				World extent = optional.get();

				String[] coords = args[1].split("\\.");

				fill.add(new Location<World>(extent, Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
			}

			String particle = container.getString(PARTICLE).get();
			String color = container.getString(COLOR).get();

			if(container.contains(SERVER)) {
				server = Optional.of(container.getString(SERVER).get());
				
				return Optional.of(new Portal(server, world, location, frame, fill, Particles.get(particle).get(), ParticleColor.get(color), rotation, price));
			}
			
			if(container.contains(WORLD)) {
				String worldName = container.getString(WORLD).get();
				
				Optional<World> optional = Sponge.getServer().getWorld(worldName);
				
				if(optional.isPresent()) {
					world = optional;
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
			
			return Optional.of(new Portal(server, world, location, frame, fill, Particles.get(particle).get(), ParticleColor.get(color), rotation, price));
		}

		return Optional.empty();
	}
}
