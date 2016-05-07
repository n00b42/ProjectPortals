package com.gmail.trentech.pjp.portals;

import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.DataQueries;
import com.gmail.trentech.pjp.utils.Rotation;

public class Home implements DataSerializable {

	private String destination;
	private String rotation;
	
	public Home(Location<World> destination, Rotation rotation){
		this.destination = destination.getExtent().getName() + ":" + destination.getBlockX() + "." + destination.getBlockY() + "." + destination.getBlockZ() ;
		this.rotation = rotation.getName();
	}
	
	public Home(String destination, String rotation){
		this.destination = destination;
		this.rotation = rotation;
	}
	
	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
			return Optional.empty();
		}
		World world = Main.getGame().getServer().getWorld(args[0]).get();

		try{
			String[] coords = args[1].split("\\.");
			
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
				
			return Optional.of(world.getLocation(x, y, z));	
		}catch(Exception e){
			return Optional.empty();
		}
	}

	public Optional<Rotation> getRotation(){
		return Rotation.get(rotation);
	}

	@Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(DataQueries.DESTINATION, destination).set(DataQueries.ROTATION, rotation);
    }
}
