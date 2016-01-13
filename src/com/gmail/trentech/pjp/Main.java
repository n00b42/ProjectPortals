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
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.CuboidListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.listeners.TeleportListener;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;

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
    	getLog().info("Initializing...");
    	fixPath();
    	
    	ConfigurationNode config = new ConfigManager().getConfig();
    	
    	getGame().getEventManager().registerListeners(this, new TeleportListener());
    	getGame().getCommandManager().register(this, new CMDBack().cmdBack, "back", config.getNode("Options", "Command-Alias", "back").getString());
    	getGame().getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");
    	
    	ConfigurationNode modules = config.getNode("Options", "Modules");
    	
    	if(modules.getNode("Cubes").getBoolean()){  		
    		getGame().getEventManager().registerListeners(this, new CuboidListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdCube, "cube", config.getNode("Options", "Command-Alias", "cube").getString());
    		getLog().info("Cube module activated");
    	}
    	if(modules.getNode("Portals").getBoolean()){  		
    		getGame().getEventManager().registerListeners(this, new PortalListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdPortal, "portal", config.getNode("Options", "Command-Alias", "portal").getString());
    		getLog().info("Portal module activated");
    	}
    	if(modules.getNode("Buttons").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new ButtonListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdButton, "button", config.getNode("Options", "Command-Alias", "button").getString());
    		getLog().info("Button module activated");
    	}
    	if(modules.getNode("Plates").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new PlateListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdPlate, "plate", config.getNode("Options", "Command-Alias", "plate").getString());
    		getLog().info("Plate module activated");
    	}
    	if(modules.getNode("Signs").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getLog().info("Sign module activated");
    	}
    	if(modules.getNode("Levers").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new LeverListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdLever, "lever", config.getNode("Options", "Command-Alias", "lever").getString());
    		getLog().info("Lever module activated");
    	}
    	if(modules.getNode("Homes").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdHome, "home", config.getNode("Options", "Command-Alias", "home").getString());
    		getLog().info("Home module activated");
    	}
    	if(modules.getNode("Warps").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdWarp, "warp", config.getNode("Options", "Command-Alias", "warp").getString());
    		getLog().info("Warp module activated");
    	}
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	
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