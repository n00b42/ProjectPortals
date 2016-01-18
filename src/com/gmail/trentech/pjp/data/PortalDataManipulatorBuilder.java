package com.gmail.trentech.pjp.data;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.util.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.Portal;

public class PortalDataManipulatorBuilder implements DataManipulatorBuilder<PortalData, ImmutablePortalData> {
    
    private Portal portal;

    @Override
    public Optional<PortalData> build(DataView container) throws InvalidDataException {
        if(!container.contains(PJPKeys.PORTAL.getQuery())) {
            return Optional.empty();
        }
        PortalData portalData = create();
        portalData = portalData.set(PJPKeys.PORTAL, (Portal) container.get(PJPKeys.PORTAL.getQuery()).get());
        return Optional.of(portalData);
    }
    
    public PortalDataManipulatorBuilder setPortal(Portal portal) {
        this.portal = portal;
        return this;
    }

    @Override
    public PortalData create() {
        return new PortalData(portal);
    }

    @Override
    public Optional<PortalData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }
}
