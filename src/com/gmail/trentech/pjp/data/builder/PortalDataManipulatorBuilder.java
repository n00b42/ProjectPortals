package com.gmail.trentech.pjp.data.builder;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.util.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.immutable.ImmutablePortalData;
import com.gmail.trentech.pjp.data.mutable.PortalData;

public class PortalDataManipulatorBuilder implements DataManipulatorBuilder<PortalData, ImmutablePortalData> {

	@Override
    public PortalData create() {
        return new PortalData();
    }

    @Override
    public Optional<PortalData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(PortalData.class).orElse(new PortalData()));
    }

    @Override
    public Optional<PortalData> build(DataView container) throws InvalidDataException {
        if (container.contains(PJPKeys.PORTAL_NAME, PJPKeys.DESTINATION)) {
            final String name = container.getString(PJPKeys.PORTAL_NAME.getQuery()).get();
            final String destination = container.getString(PJPKeys.DESTINATION.getQuery()).get();

            return Optional.of(new PortalData(name, destination));
        }
        return Optional.empty();
    }
}
