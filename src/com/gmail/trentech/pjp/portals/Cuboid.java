package com.gmail.trentech.pjp.portals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class Cuboid implements Iterable<BlockSnapshot> {

	protected final World world;
	protected final World destWorld;
	protected final Optional<Location<World>> destination;
	protected final LocationType locationType;

	protected BlockState block = BlockState.builder().blockType(BlockTypes.STONE).build();
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;

	public Cuboid(BlockState block, LocationType locationType, World world, Optional<Location<World>> destination, Location<World> loc1, Location<World> loc2) {
		if(block != null){
			this.block = block;
		}
		
		this.destination = destination;
		this.locationType = locationType;
		this.destWorld = world;
		this.world = loc1.getExtent();
		
		this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
		this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
		this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
		this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
		this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	}

	public Iterator<BlockSnapshot> iterator() {
		return new CuboidIterator(world, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
	}

	@Override
	public String toString() {
		return new String("Cuboid: " + this.world.getName() + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
	}

	public class CuboidIterator implements Iterator<BlockSnapshot> {
		private World world;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;

		public CuboidIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
			this.world = world;
			this.baseX = x1;
			this.baseY = y1;
			this.baseZ = z1;
			this.sizeX = Math.abs(x2 - x1) + 1;
			this.sizeY = Math.abs(y2 - y1) + 1;
			this.sizeZ = Math.abs(z2 - z1) + 1;
			this.x = this.y = this.z = 0;
		}

		public boolean hasNext() {
			return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
		}

		public BlockSnapshot next() {
			BlockSnapshot block = this.world.createSnapshot(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
			if (++x >= this.sizeX) {
				this.x = 0;
				if (++this.y >= this.sizeY) {
					this.y = 0;
					++this.z;
				}
			}
			return block;
		}
	}

	public Optional<List<String>> getLocations(){
		List<String> locations = new ArrayList<>();
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
	    for (BlockSnapshot block : this){
	    	Location<World> location = block.getLocation().get();

	    	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(config.getNode("Cuboids", locationName, "World").getString() == null){
				Optional.empty();
			}

			locations.add(locationName);
	    }
	    return Optional.of(locations);
	}
	
	public void build(){
        List<String> locations = getLocations().get();

        for(String loc : locations){
        	String[] info = loc.split("\\.");

        	Location<World> location = Main.getGame().getServer().getWorld(info[0]).get().getLocation(Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]));
        	
        	ConfigurationNode config = new ConfigManager().getConfig();
        	
        	if(config.getNode("Options", "Cube", "Replace-Frame").getBoolean()){	
            	if(!location.getBlockType().equals(BlockTypes.AIR)){
            		location.setBlock(block);
            	}else if(config.getNode("Options", "Cube", "Fill").getBoolean()){
//            		BlockState block = BlockState.builder().blockType(BlockTypes.FLOWING_WATER).build();
//            		location.setBlock(block);
            		// BROKEN
            	}
        	}
        	
    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Resource.spawnParticles(location, 1.0, false);
    		}
        }
        
        String uuid = UUID.randomUUID().toString();
        
        ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();
		
		config.getNode("Cuboids", uuid, "Locations").setValue(locations);
		config.getNode("Cuboids", uuid, "World").setValue(destWorld.getName());
		
        if(locationType.equals(LocationType.SPAWN)){  	
        	config.getNode("Cuboids", uuid, "Random").setValue(false);
        }else if(locationType.equals(LocationType.RANDOM)){
            config.getNode("Cuboids", uuid, "Random").setValue(true);
        }else{
        	config.getNode("Cuboids", uuid, "Random").setValue(false);
            config.getNode("Cuboids", uuid, "X").setValue(destination.get().getBlockX());
            config.getNode("Cuboids", uuid, "Y").setValue(destination.get().getBlockY());
            config.getNode("Cuboids", uuid, "Z").setValue(destination.get().getBlockZ());
        }

        configManager.save();
	}

}
