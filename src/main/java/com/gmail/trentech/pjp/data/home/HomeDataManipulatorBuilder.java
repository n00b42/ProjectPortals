package com.gmail.trentech.pjp.data.home;

import static com.gmail.trentech.pjp.data.Keys.HOME_LIST;

import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.portals.Home;

public class HomeDataManipulatorBuilder implements DataManipulatorBuilder<HomeData, ImmutableHomeData> {

	@Override
    public Optional<HomeData> build(DataView container) throws InvalidDataException {
        if (!container.contains(HOME_LIST)) {
            return Optional.empty();
        }
        
        HashMap<String, Home> homeList = new HashMap<>();
        
        DataView homes = container.getView(HOME_LIST.getQuery()).get();
        
        for(DataQuery home : homes.getKeys(false)){
        	homeList.put(home.toString(), homes.getSerializable(home, Home.class).get());
        }

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
