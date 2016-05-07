package com.gmail.trentech.pjp.data.home;

import static com.gmail.trentech.pjp.data.DataQueries.DESTINATION;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;

import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.portals.Home;

public class HomeBuilder extends AbstractDataBuilder<Home> {

    public HomeBuilder() {
        super(Home.class, 1);
    }

    @Override
    protected Optional<Home> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(DESTINATION, ROTATION)) {
            Home home = new Home(container.getString(DESTINATION).get(), container.getString(ROTATION).get());
            return Optional.of(home);
        }
        return Optional.empty();
    }
}
