package com.gmail.trentech.pjp.portals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class Cuboid implements Iterable<BlockSnapshot> {

	protected final String worldName;
	protected final Location<World> destination;
	protected final boolean spawn;

	protected BlockState block = BlockState.builder().blockType(BlockTypes.STONE).build();
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;

	public Cuboid(BlockState block, Location<World> destination, Location<World> loc1, Location<World> loc2, boolean spawn) {
		if (!loc1.getExtent().equals(loc2.getExtent())){
			throw new IllegalArgumentException("Locations must be on the same world");
		}
		this.destination = destination;
		this.spawn = spawn;

		if(block != null){
			this.block = block;
		}
		
		this.worldName = loc1.getExtent().getName();
		this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
		this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
		this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
		this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
		this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	}
	
	public Cuboid(BlockState block, String worldName, Location<World> destination, int x1, int y1, int z1, int x2, int y2, int z2, boolean spawn) {
		if(block != null){
			this.block = block;
		}
		this.worldName = worldName;
		this.destination = destination;
		this.spawn = spawn;
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
		this.z2 = Math.max(z1, z2);
	}

	private World getWorld() {
		if (!Main.getGame().getServer().getWorld(worldName).isPresent()) {
			throw new IllegalStateException("World " + this.worldName + " is not loaded");
		}
		return Main.getGame().getServer().getWorld(worldName).get();
	}

	public Iterator<BlockSnapshot> iterator() {
		return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
	}

	@Override
	public String toString() {
		return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
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
			BlockSnapshot b = this.world.createSnapshot(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
			if (++x >= this.sizeX) {
				this.x = 0;
				if (++this.y >= this.sizeY) {
					this.y = 0;
					++this.z;
				}
			}
			return b;
		}
	}

	public List<String> getLocations(){
		List<String> locations = new ArrayList<>();
	    for (BlockSnapshot block : this){
	    	Location<World> location = block.getLocation().get();

	    	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	    	ConfigManager loader = new ConfigManager("portals.conf");
			
			if(loader.getCuboid(locationName) != null){
				return null;
			}
			
			locations.add(locationName);
	    }
	    return locations;
	}
	
	public void build(){

        List<String> locations = getLocations();

        for(String loc : locations){
        	String[] info = loc.split("\\.");

        	Location<World> location = Main.getGame().getServer().getWorld(info[0]).get().getLocation(Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]));
        	
        	ConfigurationNode config = new ConfigManager().getConfig();
        	
        	if(config.getNode("Options", "Cube", "Replace-Frame").getBoolean()){	
            	if(!location.getBlockType().equals(BlockTypes.AIR)){
            		location.setBlock(block);
            	}else if(config.getNode("Options", "Cube", "Fill").getBoolean()){
            		BlockState block = BlockState.builder().blockType(BlockTypes.FLOWING_WATER).build();
            		location.setBlock(block);
            	}
        	}
        	
    		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
    			Resource.spawnParticles(location, 1.0, false);
    		}
        }
        
        String uuid = UUID.randomUUID().toString();
        
        ConfigManager loaderCuboids = new ConfigManager("portals.conf");
		ConfigurationNode configCuboids = loaderCuboids.getConfig();
		
        configCuboids.getNode("Cuboids", uuid, "Locations").setValue(locations);
        configCuboids.getNode("Cuboids", uuid, "World").setValue(destination.getExtent().getName());
        
        if(!spawn){
            configCuboids.getNode("Cuboids", uuid, "X").setValue(destination.getBlockX());
            configCuboids.getNode("Cuboids", uuid, "Y").setValue(destination.getBlockY());
            configCuboids.getNode("Cuboids", uuid, "Z").setValue(destination.getBlockZ());
        }

        loaderCuboids.save();
	}

}
