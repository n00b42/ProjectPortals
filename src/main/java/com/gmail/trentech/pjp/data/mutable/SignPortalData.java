package com.gmail.trentech.pjp.data.mutable;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.immutable.ImmutableSignPortalData;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.Utils;
import com.google.common.base.Objects;

public class SignPortalData extends AbstractData<SignPortalData, ImmutableSignPortalData> {

	private String destination;
	private String rotation;
	private double price;
	
	public SignPortalData() {
		this("", Rotation.EAST, 0);
	}

	public SignPortalData(String destination, Rotation rotation, double price) {
		this.destination = destination;
		this.rotation = rotation.getName();
		this.price = price;
	}

	public Value<String> destination() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.DESTINATION, this.destination);
    }
	
	public Value<String> rotation() {
		return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.ROTATION, this.rotation);
	}
	
	public Value<Double> price() {
		return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PRICE, this.price);
	}
	
	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
			return Optional.empty();
		}
		World world = Main.getGame().getServer().getWorld(args[0]).get();
		
		if(args[1].equalsIgnoreCase("random")){
			return Optional.of(Utils.getRandomLocation(world));
		}else if(args[1].equalsIgnoreCase("spawn")){
			return Optional.of(world.getSpawnLocation());
		}else{
			String[] coords = args[1].split("\\.");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			return Optional.of(world.getLocation(x, y, z));	
		}
	}
	
	public Rotation getRotation(){
		return Rotation.get(rotation).get();
	}
	
	@Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(PJPKeys.DESTINATION, () -> this.destination);
        registerFieldSetter(PJPKeys.DESTINATION, value -> this.destination = value);
        registerKeyValue(PJPKeys.DESTINATION, this::destination);
        
        registerFieldGetter(PJPKeys.ROTATION, () -> this.rotation);
        registerFieldSetter(PJPKeys.ROTATION, value -> this.rotation = value);
        registerKeyValue(PJPKeys.ROTATION, this::rotation);
        
        registerFieldGetter(PJPKeys.PRICE, () -> this.price);
        registerFieldSetter(PJPKeys.PRICE, value -> this.price = value);
        registerKeyValue(PJPKeys.PRICE, this::price);
    }
	
	@Override
    public Optional<SignPortalData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<SignPortalData> from(DataContainer container) {
        if (!container.contains(PJPKeys.DESTINATION.getQuery(), PJPKeys.ROTATION.getQuery(), PJPKeys.PRICE.getQuery())) {
            return Optional.empty();
        }

        destination = container.getString(PJPKeys.DESTINATION.getQuery()).get();
        rotation = container.getString(PJPKeys.ROTATION.getQuery()).get();
        price = container.getDouble(PJPKeys.PRICE.getQuery()).get();
        
        return Optional.of(this);
    }

    @Override
    public SignPortalData copy() {
        return new SignPortalData(this.destination, Rotation.get(this.rotation).get(), this.price);
    }

    @Override
    public ImmutableSignPortalData asImmutable() {
        return new ImmutableSignPortalData(this.destination, Rotation.get(this.rotation).get(), this.price);
    }

    @Override
    public int compareTo(SignPortalData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(PJPKeys.DESTINATION, this.destination).set(PJPKeys.ROTATION, this.rotation).set(PJPKeys.PRICE, this.price);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("destination", this.destination).add("rotation", this.rotation).add("price", this.price).toString();
    }
}
