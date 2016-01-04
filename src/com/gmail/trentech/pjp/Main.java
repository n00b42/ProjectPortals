package com.gmail.trentech.pjp;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.pjp.commands.CommandManager;
import com.gmail.trentech.pjp.listeners.ButtonEventManager;
import com.gmail.trentech.pjp.listeners.CuboidEventManager;
import com.gmail.trentech.pjp.listeners.EventManager;
import com.gmail.trentech.pjp.listeners.PlateEventManager;
import com.gmail.trentech.pjp.listeners.SignEventManager;

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
    	getGame().getEventManager().registerListeners(this, new EventManager());
    	getGame().getEventManager().registerListeners(this, new SignEventManager());
    	getGame().getEventManager().registerListeners(this, new CuboidEventManager());
    	getGame().getEventManager().registerListeners(this, new ButtonEventManager());
    	getGame().getEventManager().registerListeners(this, new PlateEventManager());
    	
    	getGame().getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");
    	getGame().getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "wp");
    	getGame().getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");
    	getGame().getCommandManager().register(this, new CommandManager().cmdTeleportUnSafe, "tu");

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
}