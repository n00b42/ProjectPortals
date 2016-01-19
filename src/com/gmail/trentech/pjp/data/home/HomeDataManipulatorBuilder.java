package com.gmail.trentech.pjp.data.home;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.util.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.PJPKeys;

public class HomeDataManipulatorBuilder implements DataManipulatorBuilder<HomeData, ImmutableHomeData> {

	@Override
    public HomeData create() {
        return new HomeData();
    }

    @Override
    public Optional<HomeData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(HomeData.class).orElse(new HomeData()));
    }

    @SuppressWarnings("unchecked")
	@Override
    public Optional<HomeData> build(DataView container) throws InvalidDataException {
        if (container.contains(PJPKeys.HOME_LIST)) {
            final Map<?, ?> homes = container.getMap(PJPKeys.HOME_LIST.getQuery()).get();

            return Optional.of(new HomeData((Map<String, String>) homes));
        }
        return Optional.empty();
    }
}
