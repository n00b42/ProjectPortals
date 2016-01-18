package com.gmail.trentech.pjp.data;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.pjp.data.Portal;
import com.google.common.base.Preconditions;

public class PortalData extends AbstractSingleData<Portal, PortalData, ImmutablePortalData> {

	private Portal portal;
	
    public PortalData(Portal value) {
        super(value, PJPKeys.PORTAL);
        this.portal = value;
    }

    @Override
    public PortalData copy() {
        return new PortalData(this.getValue());
    }

    @Override
    public Optional<PortalData> fill(DataHolder dataHolder, MergeFunction mergeFn) {
        PortalData portalData = Preconditions.checkNotNull(mergeFn).merge(copy(), from(dataHolder.toContainer()).orElse(null));
        return Optional.of(set(PJPKeys.PORTAL, portalData.get(PJPKeys.PORTAL).get()));
    }

    @Override
    public Optional<PortalData> from(DataContainer container) {
        if (container.contains(PJPKeys.PORTAL.getQuery())) {
            return Optional.of(set(PJPKeys.PORTAL, (Portal) container.get(PJPKeys.PORTAL.getQuery()).orElse(null)));
        }
        return Optional.empty();
    }

    @Override
    public int getContentVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ImmutablePortalData asImmutable() {
        return new ImmutablePortalData(this.getValue());
    }

    @Override
    public int compareTo(PortalData arg0) {
        return 0;
    }

    @Override
    protected Value<Portal> getValueGetter() {
    	return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PORTAL, this.getValue());
    }
    
    public Portal portal(){
    	return portal;
    }
}
