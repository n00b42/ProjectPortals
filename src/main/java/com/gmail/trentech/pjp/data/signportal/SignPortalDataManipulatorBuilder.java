package com.gmail.trentech.pjp.data.signportal;

import static com.gmail.trentech.pjp.data.Keys.SIGN;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.portals.Sign;

public class SignPortalDataManipulatorBuilder implements DataManipulatorBuilder<SignPortalData, ImmutableSignPortalData> {

    @Override
    public Optional<SignPortalData> build(DataView container) throws InvalidDataException {
        if (!container.contains(SIGN.getQuery())) {
            return Optional.empty();
        }
        Sign sign = container.getSerializable(SIGN.getQuery(), Sign.class).get();
        return Optional.of(new SignPortalData(sign));
    }

    @Override
    public SignPortalData create() {
        return new SignPortalData(new Sign("", "", 0));
    }

    @Override
    public Optional<SignPortalData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    public SignPortalData createFrom(Sign sign) {
        return new SignPortalData(sign);
    }

}
