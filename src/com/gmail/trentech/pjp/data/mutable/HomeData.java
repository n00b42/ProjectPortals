package com.gmail.trentech.pjp.data.mutable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.immutable.ImmutableHomeData;
import com.gmail.trentech.pjp.utils.Rotation;
import com.google.common.base.Objects;

public class HomeData extends AbstractData<HomeData, ImmutableHomeData> {

	private Map<String, String> homes = new HashMap<>();

	public HomeData() {

	}
	
	public HomeData(Map<String, String> homes) {
		this.homes = homes;
	}

	public MapValue<String, String> homes() {
        return Sponge.getRegistry().getValueFactory().createMapValue(PJPKeys.HOME_LIST, this.homes);
    }

	public void addHome(String name, Location<World> location, Rotation rotation) {
		String destination = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ() + ":" + rotation.getName();
		Map<String, String> newHomes = homes().get();
		newHomes.put(name, destination);
		
		this.homes = newHomes;
	}
	
	public void addHome(String name, String destination) {
		Map<String, String> newHomes = homes().get();
		newHomes.put(name, destination);
		
		this.homes = newHomes;
	}
	
	public void removeHome(String name) {
		Map<String, String> newHomes = homes().get();
		newHomes.remove(name);
		
		this.homes = newHomes;
	}
	
	public Optional<Location<World>> getDestination(String name) {
		if(!homes.containsKey(name)){
			return Optional.empty();
		}
		String[] args = homes.get(name).split(":");
		
		if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
			return Optional.empty();
		}
		World world = Main.getGame().getServer().getWorld(args[0]).get();
		
		String[] coords = args[1].split("\\.");
		
		int x = Integer.parseInt(coords[0]);
		int y = Integer.parseInt(coords[1]);
		int z = Integer.parseInt(coords[2]);
			
		return Optional.of(world.getLocation(x, y, z));	
	}
	
	public Optional<Vector3d> getRotation(String name){
		if(!homes.containsKey(name)){
			return Optional.empty();
		}
		String[] args = homes.get(name).split(":");
		
		if(args.length != 3){
			return Optional.empty();
		}
		
		Optional<Rotation> optional = Rotation.get(args[2]);
		
		if(!optional.isPresent()){
			return Optional.empty();
		}
		
		return Optional.of(new Vector3d(0,optional.get().getValue(),0));
	}
	
	@Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(PJPKeys.HOME_LIST, () -> this.homes);
        registerFieldSetter(PJPKeys.HOME_LIST, value -> this.homes = value);
        registerKeyValue(PJPKeys.HOME_LIST, this::homes);
    }
	
	@Override
    public Optional<HomeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
	@Override
    public Optional<HomeData> from(DataContainer container) {
        if (!container.contains(PJPKeys.HOME_LIST.getQuery())) {
            return Optional.empty();
        }
        homes = (Map<String, String>) container.getMap(PJPKeys.HOME_LIST.getQuery()).get();

        return Optional.of(this);
    }

    @Override
    public HomeData copy() {
        return new HomeData(this.homes);
    }

    @Override
    public ImmutableHomeData asImmutable() {
        return new ImmutableHomeData(this.homes);
    }

    @Override
    public int compareTo(HomeData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(PJPKeys.HOME_LIST, this.homes);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("homes", this.homes).toString();
    }
}
