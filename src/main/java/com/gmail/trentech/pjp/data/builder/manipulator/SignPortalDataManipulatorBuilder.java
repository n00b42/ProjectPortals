package com.gmail.trentech.pjp.data.builder.manipulator;

import static com.gmail.trentech.pjp.data.Keys.SIGN;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.immutable.ImmutableSignPortalData;
import com.gmail.trentech.pjp.data.mutable.SignPortalData;
import com.gmail.trentech.pjp.data.portal.Sign;
import com.gmail.trentech.pjp.rotation.Rotation;

public class SignPortalDataManipulatorBuilder extends AbstractDataBuilder<SignPortalData> implements DataManipulatorBuilder<SignPortalData, ImmutableSignPortalData> {

	public SignPortalDataManipulatorBuilder() {
		super(SignPortalData.class, 1);
	}
	
	@Override
	public Optional<SignPortalData> buildContent(DataView container) throws InvalidDataException {
		if (!container.contains(SIGN.getQuery())) {
			return Optional.empty();
		}
		Sign sign = container.getSerializable(SIGN.getQuery(), Sign.class).get();
		return Optional.of(new SignPortalData(sign));
	}

	@Override
	public SignPortalData create() {
		return new SignPortalData(new Sign(Optional.empty(), Optional.empty(), Optional.empty(), Rotation.EAST, 0));
	}

	@Override
	public Optional<SignPortalData> createFrom(DataHolder dataHolder) {
		return create().fill(dataHolder);
	}

	public SignPortalData createFrom(Sign sign) {
		return new SignPortalData(sign);
	}

}
