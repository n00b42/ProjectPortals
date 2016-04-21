package com.gmail.trentech.pjp.data.builder;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.immutable.ImmutableSignPortalData;
import com.gmail.trentech.pjp.data.mutable.SignPortalData;
import com.gmail.trentech.pjp.utils.Rotation;

public class SignPortalDataManipulatorBuilder implements DataManipulatorBuilder<SignPortalData, ImmutableSignPortalData> {

	@Override
    public SignPortalData create() {
        return new SignPortalData();
    }

    @Override
    public Optional<SignPortalData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(SignPortalData.class).orElse(new SignPortalData()));
    }

    @Override
    public Optional<SignPortalData> build(DataView container) throws InvalidDataException {
        if (container.contains(PJPKeys.DESTINATION, PJPKeys.ROTATION, PJPKeys.PRICE)) {
            final String destination = container.getString(PJPKeys.DESTINATION.getQuery()).get();
            final String rotation = container.getString(PJPKeys.ROTATION.getQuery()).get();
            final double price = container.getDouble(PJPKeys.PRICE.getQuery()).get();
            
            return Optional.of(new SignPortalData(destination, Rotation.get(rotation).get(), price));
        }
        return Optional.empty();
    }
}
