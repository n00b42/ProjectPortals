package com.gmail.trentech.pjp.data.signportal;

import static com.gmail.trentech.pjp.data.DataQueries.DESTINATION;
import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;

import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.portals.Sign;

public class SignBuilder extends AbstractDataBuilder<Sign> {

    public SignBuilder() {
        super(Sign.class, 1);
    }

    @Override
    protected Optional<Sign> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(DESTINATION, ROTATION, PRICE)) {
            Sign signPortal = new Sign(container.getString(DESTINATION).get(), container.getString(ROTATION).get(), container.getDouble(PRICE).get());
            return Optional.of(signPortal);
        }
        return Optional.empty();
    }
}
