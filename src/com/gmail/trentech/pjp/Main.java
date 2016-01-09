package com.gmail.trentech.pjp;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.pjp.commands.CMDBack;
import com.gmail.trentech.pjp.commands.CommandManager;
import com.gmail.trentech.pjp.listeners.ButtonEventManager;
import com.gmail.trentech.pjp.listeners.CuboidEventManager;
import com.gmail.trentech.pjp.listeners.EventManager;
import com.gmail.trentech.pjp.listeners.PlateEventManager;
import com.gmail.trentech.pjp.listeners.SignEventManager;

import ninja.leaping.configurate.ConfigurationNode;

@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION)
public class Main {

	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getGame().getPluginManager().getLogger(plugin);
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	fixPath();
    	getGame().getEventManager().registerListeners(this, new EventManager());
    	getGame().getEventManager().registerListeners(this, new SignEventManager());
    	getGame().getEventManager().registerListeners(this, new CuboidEventManager());
    	getGame().getEventManager().registerListeners(this, new ButtonEventManager());
    	getGame().getEventManager().registerListeners(this, new PlateEventManager());
    	
    	ConfigurationNode config = new ConfigManager().getConfig();
    	
    	getGame().getCommandManager().register(this, new CommandManager().cmdPortal, "portal", config.getNode("Options", "Command-Alias", "portal").getString());
    	getGame().getCommandManager().register(this, new CommandManager().cmdWarp, "warp", config.getNode("Options", "Command-Alias", "warp").getString());
    	getGame().getCommandManager().register(this, new CommandManager().cmdHome, "home", config.getNode("Options", "Command-Alias", "home").getString());
    	getGame().getCommandManager().register(this, new CMDBack().cmdBack, "back", config.getNode("Options", "Command-Alias", "back").getString());
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	getLog().info("Initializing...");

    	new ConfigManager();
    }

	public static Logger getLog() {
        return log;
    }
    
	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}
	
	private void fixPath(){
		File directory = new File("config", "Project Portals");
		if(directory.exists()){
			File newDirectory = new File("config", "projectportals");
			directory.renameTo(newDirectory);
		}
	}
}