package com.gmail.trentech.pjp.data.home;

import static com.gmail.trentech.pjp.data.Keys.HOME_LIST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.portals.Home;

public class HomeDataManipulatorBuilder implements DataManipulatorBuilder<HomeData, ImmutableHomeData> {

    @SuppressWarnings("unchecked")
	@Override
    public Optional<HomeData> build(DataView container) throws InvalidDataException {
        if (!container.contains(HOME_LIST)) {
            return Optional.empty();
        }
        Map<String, Home> homeList = (Map<String, Home>) container.getMap(HOME_LIST.getQuery()).get();
        return Optional.of(new HomeData(homeList));
    }

    @Override
    public HomeData create() {
        return new HomeData(new HashMap<String, Home>());
    }

    @Override
    public Optional<HomeData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

}
