package com.gmail.trentech.pjp.data.portal;

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
import com.gmail.trentech.pjp.utils.Utils;
import com.google.common.base.Objects;

public class PortalData extends AbstractData<PortalData, ImmutablePortalData> {

	private String name;
	private String destination;

	public PortalData() {
		this("","");
	}
	
	public PortalData(String name, World world, boolean random) {
		this.name = name;
		if(random){
			this.destination = world.getName() + ":random";
		}else{
			this.destination = world.getName() + ":spawn";
		}
	}
	
	public PortalData(String name, Location<World> destination) {
		this.name = name;
		this.destination = destination.getExtent().getName() + ":" + destination.getBlockX() + "." + destination.getBlockY() + "." + destination.getBlockZ();
	}
	
	public PortalData(String name, String destination) {
		this.name = name;
		this.destination = destination;
	}

	public Value<String> name() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PORTAL_NAME, this.name);
    }
	
	public Value<String> destination() {
        return Sponge.getRegistry().getValueFactory().createValue(PJPKeys.PORTAL_NAME, this.destination);
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
	
	@Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(PJPKeys.PORTAL_NAME, () -> this.name);
        registerFieldSetter(PJPKeys.PORTAL_NAME, value -> this.name = value);
        registerKeyValue(PJPKeys.PORTAL_NAME, this::name);

        registerFieldGetter(PJPKeys.DESTINATION, () -> this.destination);
        registerFieldSetter(PJPKeys.DESTINATION, value -> this.destination = value);
        registerKeyValue(PJPKeys.DESTINATION, this::destination);
    }
	
	@Override
    public Optional<PortalData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<PortalData> from(DataContainer container) {
        if (!container.contains(PJPKeys.PORTAL_NAME.getQuery(), PJPKeys.DESTINATION.getQuery())) {
            return Optional.empty();
        }
        name = container.getString(PJPKeys.PORTAL_NAME.getQuery()).get();
        destination = container.getString(PJPKeys.DESTINATION.getQuery()).get();
        
        return Optional.of(this);
    }

    @Override
    public PortalData copy() {
        return new PortalData(this.name, this.destination);
    }

    @Override
    public ImmutablePortalData asImmutable() {
        return new ImmutablePortalData(this.name, this.destination);
    }

    @Override
    public int compareTo(PortalData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(PJPKeys.PORTAL_NAME, this.name).set(PJPKeys.DESTINATION, this.destination);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", this.name).add("destination", this.destination).toString();
    }
}
