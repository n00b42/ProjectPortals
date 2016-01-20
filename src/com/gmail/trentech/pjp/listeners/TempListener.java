package com.gmail.trentech.pjp.listeners;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class TempListener {

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event, @First Player player){
		ConfigurationNode config = new ConfigManager("Players", player.getUniqueId().toString() + ".conf").getConfig();
		
		Map<Object, ? extends ConfigurationNode> homes = config.getNode("Homes").getChildrenMap();
		if(!homes.isEmpty()){
			HomeData homeData;

			Optional<HomeData> optionalHomeData = player.get(HomeData.class);
			
			if(optionalHomeData.isPresent()){
				homeData = optionalHomeData.get();
			}else{
				homeData = new HomeData();
			}
			
			for(Entry<Object, ? extends ConfigurationNode> home : homes.entrySet()){
				String name = home.getKey().toString();
				
				String worldName = config.getNode("Homes", name, "World").getString();
				
				int x = config.getNode("Homes", name, "X").getInt();
				int y = config.getNode("Homes", name, "Y").getInt();
				int z = config.getNode("Homes", name, "Z").getInt();

				String location = worldName + ":" + x + "." + y + "." + z;

				homeData.addHome(name, location);
				
				String folder = "config" + File.separator + "projectportals" + File.separator + "Players";
				
		        new File(folder, player.getUniqueId().toString() + ".conf").delete();
			}
			
			player.offer(homeData);
		}

	}
}
