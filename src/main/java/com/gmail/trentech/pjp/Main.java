package com.gmail.trentech.pjp;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.pjp.commands.CMDBack;
import com.gmail.trentech.pjp.commands.CommandManager;
import com.gmail.trentech.pjp.data.home.HomeBuilder;
import com.gmail.trentech.pjp.data.home.HomeData;
import com.gmail.trentech.pjp.data.home.HomeDataManipulatorBuilder;
import com.gmail.trentech.pjp.data.home.ImmutableHomeData;
import com.gmail.trentech.pjp.data.signportal.ImmutableSignPortalData;
import com.gmail.trentech.pjp.data.signportal.SignBuilder;
import com.gmail.trentech.pjp.data.signportal.SignPortalData;
import com.gmail.trentech.pjp.data.signportal.SignPortalDataManipulatorBuilder;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.listeners.TeleportListener;
import com.gmail.trentech.pjp.portals.Button;
import com.gmail.trentech.pjp.portals.Door;
import com.gmail.trentech.pjp.portals.Home;
import com.gmail.trentech.pjp.portals.Lever;
import com.gmail.trentech.pjp.portals.Plate;
import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.portals.Sign;
import com.gmail.trentech.pjp.portals.Warp;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;
import net.minecrell.mcstats.SpongeStatsLite;
import ninja.leaping.configurate.ConfigurationNode;

@Updatifier(repoName = "ProjectPortals", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, authors = Resource.AUTHOR, url = Resource.URL, description = Resource.DESCRIPTION, dependencies = {@Dependency(id = "Updatifier", optional = true)})
public class Main {

    @Inject
    private SpongeStatsLite stats;
    
	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
		
		if(this.stats.start()) {
			getLog().info("MCStats started.");
		}else{
			getLog().warn("Could not start MCStats. This could be due to server opt-out, or error.");
		}
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	ConfigurationNode config = new ConfigManager().getConfig();
    	ConfigurationNode modules = config.getNode("settings", "modules");
    	
    	getGame().getEventManager().registerListeners(this, new TeleportListener());
    	
    	getGame().getCommandManager().register(this, new CMDBack().cmdBack, "back");
    	getGame().getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");

    	if(modules.getNode("portals").getBoolean()) {  		
    		getGame().getEventManager().registerListeners(this, new PortalListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");
    		getLog().info("Portal module activated");
    	}
    	if(modules.getNode("buttons").getBoolean()) {
    		getGame().getEventManager().registerListeners(this, new ButtonListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdButton, "button", "b");
    		getLog().info("Button module activated");
    	}
    	if(modules.getNode("doors").getBoolean()) {
    		getGame().getEventManager().registerListeners(this, new DoorListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdDoor, "door", "d");
    		getLog().info("Door module activated");
    	}
    	if(modules.getNode("plates").getBoolean()) {
    		getGame().getEventManager().registerListeners(this, new PlateListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdPlate, "plate", "pp");
    		getLog().info("Plate module activated");
    	}
    	if(modules.getNode("signs").getBoolean()) {
        	getGame().getDataManager().register(SignPortalData.class, ImmutableSignPortalData.class, new SignPortalDataManipulatorBuilder());
        	getGame().getDataManager().registerBuilder(Sign.class, new SignBuilder());
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdSign, "sign", "s");
    		getLog().info("Sign module activated");
    	}
    	if(modules.getNode("levers").getBoolean()) {
    		getGame().getEventManager().registerListeners(this, new LeverListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdLever, "lever", "l");
    		getLog().info("Lever module activated");
    	}
    	if(modules.getNode("homes").getBoolean()) {
    		getGame().getDataManager().register(HomeData.class, ImmutableHomeData.class, new HomeDataManipulatorBuilder());
    		getGame().getDataManager().registerBuilder(Home.class, new HomeBuilder());
    		getGame().getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");
    		getLog().info("Home module activated");
    	}
    	if(modules.getNode("warps").getBoolean()) {
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "w");
    		getLog().info("Warp module activated");
    	}
    	
    	SQLUtils.createTables(modules);
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	ConfigurationNode modules = new ConfigManager().getConfig().getNode("settings", "modules");

    	if(modules.getNode("portals").getBoolean()) {
    		Portal.init();
    	}
    	if(modules.getNode("buttons").getBoolean()) {
    		Button.init();
    	}
    	if(modules.getNode("doors").getBoolean()) {
    		Door.init();
    	}
    	if(modules.getNode("plates").getBoolean()) {
    		Plate.init();
    	}
    	if(modules.getNode("levers").getBoolean()) {
    		Lever.init();
    	}
    	if(modules.getNode("warps").getBoolean()) {
    		Warp.init();
    	}
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