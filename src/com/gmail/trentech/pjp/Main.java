package com.gmail.trentech.pjp;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.commands.CMDBack;
import com.gmail.trentech.pjp.commands.CommandManager;
import com.gmail.trentech.pjp.data.home.HomeData;
import com.gmail.trentech.pjp.data.home.HomeDataManipulatorBuilder;
import com.gmail.trentech.pjp.data.home.ImmutableHomeData;
import com.gmail.trentech.pjp.data.portal.ImmutablePortalData;
import com.gmail.trentech.pjp.data.portal.PortalData;
import com.gmail.trentech.pjp.data.portal.PortalDataManipulatorBuilder;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.CuboidListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.listeners.TeleportListener;
import com.gmail.trentech.pjp.portals.Portal;
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
    	if(modules.getNode("Doors").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new DoorListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdDoor, "door", config.getNode("Options", "Command-Alias", "door").getString());
    		getLog().info("Door module activated");
    	}
    	if(modules.getNode("Plates").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new PlateListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdPlate, "plate", config.getNode("Options", "Command-Alias", "plate").getString());
    		getLog().info("Plate module activated");
    	}
    	if(modules.getNode("Signs").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdSign, "sign");
    		getLog().info("Sign module activated");
    	}
    	if(modules.getNode("Levers").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new LeverListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdLever, "lever", config.getNode("Options", "Command-Alias", "lever").getString());
    		getLog().info("Lever module activated");
    	}
    	if(modules.getNode("Homes").getBoolean()){
    		getGame().getDataManager().register(HomeData.class, ImmutableHomeData.class, new HomeDataManipulatorBuilder());
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdHome, "home", config.getNode("Options", "Command-Alias", "home").getString());
    		getLog().info("Home module activated");
    	}
    	if(modules.getNode("Warps").getBoolean()){
    		getGame().getEventManager().registerListeners(this, new SignListener());
    		getGame().getCommandManager().register(this, new CommandManager().cmdWarp, "warp", config.getNode("Options", "Command-Alias", "warp").getString());
    		getLog().info("Warp module activated");
    	}
    	
    	getGame().getDataManager().register(PortalData.class, ImmutablePortalData.class, new PortalDataManipulatorBuilder());
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
	    	for(Portal portal : Portal.list()){
	    		createTask(portal.getName(), portal.getFill());
	    	}
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

	public static void createTask(String name, List<Location<World>> locations){
        Main.getGame().getScheduler().createTaskBuilder().interval(300, TimeUnit.MILLISECONDS).name(name).execute(new Runnable() {
        	ThreadLocalRandom random = ThreadLocalRandom.current();
        	
			@Override
            public void run() {
				for(Location<World> location : locations){
					double v1 = 0.0 + (1 - 0.0) * random.nextDouble();
					double v2 = 0.0 + (1 - 0.0) * random.nextDouble();
					double v3 = 0.0 + (1 - 0.0) * random.nextDouble();

					if(random.nextDouble() < 0.5){
						try{
							location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
									.type(ParticleTypes.PORTAL).motion(Vector3d.ZERO).offset(Vector3d.ZERO).count(2).build(), location.getPosition().add(v1,v2,v3));
							location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
									.type(ParticleTypes.PORTAL).motion(Vector3d.ZERO).offset(Vector3d.ZERO).count(2).build(), location.getPosition().add(v3,v1,v2));
							location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
									.type(ParticleTypes.PORTAL).motion(Vector3d.ZERO).offset(Vector3d.ZERO).count(2).build(), location.getPosition().add(v2,v3,v1));
						}catch(Exception e){
							 cancel(name);
						}
					}
				}

            }
        }).submit(getPlugin());
	}
	
	private static void cancel(String name){
		for(Task task : Main.getGame().getScheduler().getScheduledTasks()){
			if(task.getName().contains(name)){
				task.cancel();
			}
		}
	}
	
	private void fixPath(){
		File directory = new File("config", "Project Portals");
		if(directory.exists()){
			File newDirectory = new File("config", "projectportals");
			directory.renameTo(newDirectory);
		}
	}
}