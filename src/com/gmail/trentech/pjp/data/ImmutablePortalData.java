package com.gmail.trentech.pjp.data;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutablePortalData extends AbstractImmutableSingleData<Portal, ImmutablePortalData, PortalData> {

    protected ImmutablePortalData(Portal value) {
        super(value, PJPKeys.PORTAL);
    }

    @Override
    public <E> Optional<ImmutablePortalData> with(Key<? extends BaseValue<E>> key, E value) {
        if(this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int compareTo(ImmutablePortalData arg0) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PORTAL, this.getValue()).asImmutable();
    }

    @Override
    public PortalData asMutable() {
        return new PortalData(this.getValue());
    }
}
