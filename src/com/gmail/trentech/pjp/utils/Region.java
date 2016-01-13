package com.gmail.trentech.pjp.utils;

import java.util.Iterator;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Region implements Iterable<Location<World>> {
	
	protected final World world;
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;

	public Region(Location<World> loc1, Location<World> loc2) {
		this.world = loc1.getExtent();
		
		this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
		this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
		this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
		this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
		this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	}

	public Iterator<Location<World>> iterator() {
		return new RegionIterator(world, x1, y1, z1, x2, y2, z2);
	}
	
	@Override
	public String toString() {
		return new String("Region: " + world.getName() + "," + x1 + "," + y1 + "," + z1 + "=>" + x2 + "," + y2 + "," + z2);
	}
	
	public int size(){
		int size = 0;
		Iterator<Location<World>> iterator = this.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}
	
	public class RegionIterator implements Iterator<Location<World>> {
		private World world;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;
		
		public RegionIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
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
			return x < sizeX && y < sizeY && z < sizeZ;
		}
		
		public Location<World> next() {
			Location<World> location = world.getLocation(baseX + x, baseY + y, baseZ + z);
			if (++x >= sizeX) {
				x = 0;
				if (++y >= sizeY) {
					y = 0;
					++z;
				}
			}
			return location;
		}
	}
}
