package com.gmail.trentech.pjp.data.builder.data;

import static com.gmail.trentech.pjp.data.DataQueries.DESTINATION;
import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;

import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.object.Warp;

public class WarpBuilder extends AbstractDataBuilder<Warp> {

    public WarpBuilder() {
        super(Warp.class, 1);
    }

    @Override
    protected Optional<Warp> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(DESTINATION, ROTATION, PRICE)) {
        	String destination = container.getString(DESTINATION).get();
        	String rotation = container.getString(ROTATION).get();
        	Double price = container.getDouble(PRICE).get();
        	
            return Optional.of(new Warp(destination, rotation, price));
        }
        
        return Optional.empty();
    }
}
