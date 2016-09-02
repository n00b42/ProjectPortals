package com.gmail.trentech.pjp.listeners;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjp.data.object.Warp;

public class WarpListener {

	//@Listener
	public void onTabCompleteEvent(TabCompleteEvent event, @First CommandSource src) {
		String rawMessage = event.getRawMessage();
		
		String[] args = rawMessage.split(" ");
		
		if(args.length <= 1) {
			return;
		}
		
		if(!args[0].equalsIgnoreCase("home") && !args[0].equalsIgnoreCase("h")) {
			return;
		}
		
		List<String> list = event.getTabCompletions();
		
		if(args.length > 1 && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r"))) {
			ConcurrentHashMap<String, Warp> optionalWarpList = Warp.all();

			for(Entry<String, Warp> warp : optionalWarpList.entrySet()) {
				String name = warp.getKey();
				
				if(args.length == 3) {
					if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
						list.add(name);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(name);
				}
			}
		} else if(args.length > 1 && (args[1].equalsIgnoreCase("price") || args[1].equalsIgnoreCase("p"))) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				String name = world.getWorldName();
				
				if(args.length == 3) {
					if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
						list.add(name);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(name);
				}
			}
		} else if(args.length == 1 || args.length == 2) {
			ConcurrentHashMap<String, Warp> optionalWarpList = Warp.all();

			for(Entry<String, Warp> warp : optionalWarpList.entrySet()) {
				String name = warp.getKey();
				
				if(args.length == 2) {
					if(name.contains(args[1].toLowerCase()) && !name.equalsIgnoreCase(args[1])) {
						list.add(name);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(name);
				}
			}
		}
	}
}
