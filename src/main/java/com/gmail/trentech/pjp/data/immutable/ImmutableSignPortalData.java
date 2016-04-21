package com.gmail.trentech.pjp.data.immutable;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.mutable.SignPortalData;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.Utils;
import com.google.common.collect.ComparisonChain;

public class ImmutableSignPortalData extends AbstractImmutableData<ImmutableSignPortalData, SignPortalData> {

	private String name;
	private String destination;
	private String rotation;
	private double price;

	public ImmutableSignPortalData() {
		this("", Rotation.EAST, 0);
	}
	
	public ImmutableSignPortalData(String destination, Rotation rotation, double price) {
		this.destination = destination;
		this.rotation = rotation.getName();
		this.price = price;
	}

	public ImmutableValue<String> destination() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.DESTINATION, this.destination).asImmutable();
    }

	public ImmutableValue<String> rotation() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.ROTATION, this.rotation).asImmutable();
    }
	
	public ImmutableValue<Double> price() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PRICE, this.price).asImmutable();
    }
	
    @Override
    protected void registerGetters() {
        registerFieldGetter(PJPKeys.DESTINATION, this::getDest);
        registerKeyValue(PJPKeys.DESTINATION, this::destination);
      
        registerFieldGetter(PJPKeys.ROTATION, this::getRot);
        registerKeyValue(PJPKeys.ROTATION, this::rotation);
        
        registerFieldGetter(PJPKeys.PRICE, this::getPrice);
        registerKeyValue(PJPKeys.PRICE, this::price);
    }

    @Override
    public <E> Optional<ImmutableSignPortalData> with(Key<? extends BaseValue<E>> key, E value) {
        return Optional.empty();
    }

    @Override
    public SignPortalData asMutable() {
        return new SignPortalData(this.destination, Rotation.get(this.rotation).get(), this.price);
    }

    @Override
    public int compareTo(ImmutableSignPortalData o) {
        return ComparisonChain.start().compare(o.destination, this.destination).compare(o.rotation, this.rotation).compare(o.price, this.price).result();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(PJPKeys.DESTINATION, this.destination).set(PJPKeys.ROTATION, this.rotation).set(PJPKeys.PRICE, this.price);
    }
    
    public String getName() {
        return this.name;
    }
    
    private String getDest() {
        return this.destination;
    }
    
    private String getRot() {
        return this.rotation;
    }
    
    private double getPrice() {
        return this.price;
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
}
