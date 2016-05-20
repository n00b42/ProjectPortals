package com.gmail.trentech.pjp.data.builder.data;

import static com.gmail.trentech.pjp.data.DataQueries.DESTINATION;
import static com.gmail.trentech.pjp.data.DataQueries.FILL;
import static com.gmail.trentech.pjp.data.DataQueries.FRAME;
import static com.gmail.trentech.pjp.data.DataQueries.PARTICLE;
import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.pjp.data.object.Portal;

public class PortalBuilder extends AbstractDataBuilder<Portal> {

    public PortalBuilder() {
        super(Portal.class, 1);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected Optional<Portal> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(DESTINATION, ROTATION, FRAME, FILL, PARTICLE, PRICE)) {
        	String destination = container.getString(DESTINATION).get();
        	String rotation = container.getString(ROTATION).get();
        	List<String> frame = (List<String>) container.getList(FRAME).get();
        	List<String> fill = (List<String>) container.getList(FILL).get();
        	String particle = container.getString(PARTICLE).get();
        	Double price = container.getDouble(PRICE).get();
        	
            return Optional.of(new Portal(destination, rotation, frame, fill, particle, price));
        }
        
        return Optional.empty();
    }
}
