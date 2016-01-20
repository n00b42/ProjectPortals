package com.gmail.trentech.pjp.data.immutable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.PJPKeys;
import com.gmail.trentech.pjp.data.mutable.HomeData;

public class ImmutableHomeData extends AbstractImmutableData<ImmutableHomeData, HomeData> {

	private Map<String, String> homes = new HashMap<>();

	public ImmutableHomeData() {

	}
	
	public ImmutableHomeData(Map<String, String> homes) {
		this.homes = homes;
	}
	
	public void addHome(String name, Location<World> location) {
		String destination = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
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
	
	public ImmutableMapValue<String, String> homes() {
        return Sponge.getRegistry().getValueFactory().createMapValue(PJPKeys.HOME_LIST, this.homes).asImmutable();
    }

	public Optional<Location<World>> getHome(String name) {
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

    @Override
    protected void registerGetters() {
        registerFieldGetter(PJPKeys.HOME_LIST, this::getHomes);
        registerKeyValue(PJPKeys.HOME_LIST, this::homes);
    }

    @Override
    public <E> Optional<ImmutableHomeData> with(Key<? extends BaseValue<E>> key, E value) {
        return Optional.empty();
    }

    @Override
    public HomeData asMutable() {
        return new HomeData(this.homes);
    }

    @Override
    public int compareTo(ImmutableHomeData o) {
    	return 0;
        //return ComparisonChain.start().compare(o.homes, this.homes).result();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(PJPKeys.HOME_LIST, this.homes);
    }
    
    public Map<String, String> getHomes() {
        return this.homes;
    }
}
