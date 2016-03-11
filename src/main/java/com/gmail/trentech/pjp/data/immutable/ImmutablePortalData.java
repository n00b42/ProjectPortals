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

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.mutable.PortalData;
import com.gmail.trentech.pjp.utils.Rotation;
import com.gmail.trentech.pjp.utils.Utils;
import com.google.common.collect.ComparisonChain;

public class ImmutablePortalData extends AbstractImmutableData<ImmutablePortalData, PortalData> {

	private String name;
	private String destination;

	public ImmutablePortalData() {
		this("","");
	}
	
	public ImmutablePortalData(String name, String destination) {
		this.name = name;
		this.destination = destination;
	}

	public ImmutableValue<String> name() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PORTAL_NAME, this.name).asImmutable();
    }
	
	public ImmutableValue<String> destination() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PORTAL_NAME, this.destination).asImmutable();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(PJPKeys.PORTAL_NAME, this::getName);
        registerKeyValue(PJPKeys.PORTAL_NAME, this::name);

        registerFieldGetter(PJPKeys.DESTINATION, this::getDest);
        registerKeyValue(PJPKeys.DESTINATION, this::destination);
    }

    @Override
    public <E> Optional<ImmutablePortalData> with(Key<? extends BaseValue<E>> key, E value) {
        return Optional.empty();
    }

    @Override
    public PortalData asMutable() {
        return new PortalData(this.name, this.destination);
    }

    @Override
    public int compareTo(ImmutablePortalData o) {
        return ComparisonChain.start().compare(o.name, this.name).compare(o.destination, this.destination).result();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(PJPKeys.PORTAL_NAME, this.name).set(PJPKeys.DESTINATION, this.destination);
    }
    
    public String getName() {
        return this.name;
    }
    
    private String getDest() {
        return this.destination;
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
	
	public Optional<Vector3d> getRotation(){
		String[] args = destination.split(":");
		
		if(args.length != 3){
			return Optional.empty();
		}
		
		Optional<Rotation> optional = Rotation.get(args[2]);
		
		if(!optional.isPresent()){
			return Optional.empty();
		}
		
		return Optional.of(new Vector3d(0,optional.get().getValue(),0));
	}
}
