package com.gmail.trentech.pjp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.pjp.commands.CMDBack;
import com.gmail.trentech.pjp.commands.CommandManager;
import com.gmail.trentech.pjp.data.immutable.ImmutableHomeData;
import com.gmail.trentech.pjp.data.immutable.ImmutableSignPortalData;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.mutable.SignPortalData;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LegacyListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.listeners.TeleportListener;
import com.gmail.trentech.pjp.portal.LocationSerializable;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Properties;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Timings;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path path;

	@Inject
	private Logger log;

	private static PluginContainer plugin;
	private static Main instance;

	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;

		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		ConfigManager configManager = ConfigManager.init();
		ConfigurationNode config = configManager.getConfig();

		Timings timings = new Timings();

		Sponge.getDataManager().registerBuilder(LocationSerializable.class, new LocationSerializable.Builder());
		Sponge.getDataManager().registerBuilder(Properties.class, new Properties.Builder());
		Sponge.getDataManager().registerBuilder(Portal.Local.class, new Portal.Local.Builder());
		Sponge.getDataManager().registerBuilder(Portal.Server.class, new Portal.Server.Builder());

		Sponge.getEventManager().registerListeners(this, new TeleportListener(timings));

		Sponge.getCommandManager().register(this, new CMDBack().cmdBack, "back");
		Sponge.getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");

		ConfigurationNode modules = config.getNode("settings", "modules");

		if (modules.getNode("portals").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new PortalListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");

			if (config.getNode("options", "portal", "legacy_builder").getBoolean()) {
				Sponge.getEventManager().registerListeners(this, new LegacyListener(timings));
			}
			
			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help portalCreate = new Help("portal create", "create", "Use this command to create a portal that will teleport you to other worlds")
						.setPermission("pjp.cmd.portal.create")
						.addUsage("/portal create <name> <destination> [-b] [-c <x,y,z>] [-d <direction>] [-e <particle[:color]>] [-p <price>]")
						.addUsage("/p <name> <destination> [-b] [-c <x,y,z>] [-d <direction>] [-e <particle[:color]>] [-p <price>]")
						.addExample("/portal create MyPortal MyWorld -c -100,65,254")
						.addExample("/portal create MyPortal MyWorld -c random")
						.addExample("/portal create MyPortal MyWorld -c -100,65,254 -d south")
						.addExample("/portal create MyPortal MyWorld -d southeast")
						.addExample("/portal create MyPortal MyWorld -p 50")
						.addExample("/portal create MyPortal MyWorld -e REDSTONE:BLUE")
						.addExample("/portal create MyPortal MyWorld");
				
				Help portalDestination = new Help("portal destination", "destination", "Change as existing portals destination")
						.setPermission("pjp.cmd.portal.destination")
						.addUsage("/portal destination <name> <destination> [x,y,z]")
						.addUsage("/p d <name> <destination> [x,y,z]")
						.addExample("/portal destination Skyland 100,65,400")
						.addExample("/portal destination Server1")
						.addExample("/portal destination MyPortal DIM1");
				
				Help portalList = new Help("portal list", "list", "List all portals")
						.setPermission("pjp.cmd.portal.list")
						.addUsage("/portal list")
						.addUsage("/p ls");
				
				Help portalParticle = new Help("portal particle", "particle", "Change a portals particle effect. Color currently only available for REDSTONE")
						.setPermission("pjp.cmd.portal.particle")
						.addUsage("/portal particle <name> <type> [color]")
						.addUsage("/p p <name> <type> [color]")
						.addExample("/portal particle MyPortal REDSTONE BLUE")
						.addExample("/portal particle MyPortal CRIT");
				
				Help portalPrice = new Help("portal price", "price", "Charge players for using portals. 0 to disable")
						.setPermission("pjp.cmd.portal.price")
						.addUsage("/portal price <name> <price>")
						.addUsage("/p pr <name> <price>")
						.addExample("/portal price MyPortal 0")
						.addExample("/portal price MyPortal 50");
				
				Help portalRemove = new Help("portal remove", "remove", "Remove an existing portal")
						.setPermission("pjp.cmd.portal.remove")
						.addUsage("/portal remove <name>")
						.addUsage("/p r <name>")
						.addExample("/portal remove MyPortal");
				
				Help portalRename = new Help("portal rename", "rename", "Rename portal")
						.setPermission("pjp.cmd.portal.rename")
						.addUsage("/portal rename <oldName> <newName>")
						.addUsage("/p rn <oldName> <newName>")
						.addExample("/portal rename MyPortal ThisPortal");
					
				Help portalSave = new Help("portal save", "save", "Saves generated portal")
						.setPermission("pjp.cmd.portal.save")
						.addUsage("/portal save")
						.addUsage("/p s");
				
				Help portal = new Help("portal", "portal", " Top level portal command")
						.setPermission("pjp.cmd.portal")
						.addChild(portalSave)
						.addChild(portalRename)
						.addChild(portalRemove)
						.addChild(portalPrice)
						.addChild(portalParticle)
						.addChild(portalList)
						.addChild(portalDestination)
						.addChild(portalCreate);
				
				Help.register(portal);
			}

			getLog().info("Portal module activated");
		}
		if (modules.getNode("buttons").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new ButtonListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdButton, "button", "b");

			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help button = new Help("button", "button", "Use this command to create a button that will teleport you to other worlds")
						.setPermission("pjp.cmd.button")
						.addUsage("/button <destination>  [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]")
						.addUsage("/b <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]")
						.addExample("/button MyWorld -c random")
						.addExample("/button MyWorld -c -100,65,254 -d south")
						.addExample("/button MyWorld -d southeast")
						.addExample("/button MyWorld -c -100,65,254")
						.addExample("/button MyWorld");
				
				Help.register(button);
			}
			
			getLog().info("Button module activated");
		}
		if (modules.getNode("doors").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new DoorListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdDoor, "door", "d");

			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help door = new Help("door", "door", "Use this command to create a door that will teleport you to other worlds")
					    .setPermission("pjp.cmd.door")
						.addUsage("/door <destination>  [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]")
						.addUsage("/d <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]")
						.addExample("/door MyWorld -c random")
						.addExample("/door MyWorld -c -100,65,254 -d south")
						.addExample("/door MyWorld -d southeast")
						.addExample("/door MyWorld -c -100,65,254")
						.addExample("/door MyWorld");
				
				Help.register(door);	
			}
			
			getLog().info("Door module activated");
		}
		if (modules.getNode("plates").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new PlateListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdPlate, "plate", "pp");

			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help plate = new Help("plate", "plate", "Use this command to create a pressure plate that will teleport you to other worlds")
					    .setPermission("pjp.cmd.plate")
						.addUsage("/plate <destination>  [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]")
						.addUsage("/p <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]")
						.addExample("/plate MyWorld -c random")
						.addExample("/plate MyWorld -c -100,65,254 -d south")
						.addExample("/plate MyWorld -d southeast")
						.addExample("/plate MyWorld -c -100,65,254")
						.addExample("/plate MyWorld");
				
				Help.register(plate);
			}
			
			getLog().info("Pressure plate module activated");
		}
		if (modules.getNode("signs").getBoolean()) {
			Sponge.getDataManager().register(SignPortalData.class, ImmutableSignPortalData.class, new SignPortalData.Builder());
			Sponge.getEventManager().registerListeners(this, new SignListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdSign, "sign", "s");
			
			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help sign = new Help("sign", "sign", "Use this command to create a sign that will teleport you to other worlds")
					    .setPermission("pjp.cmd.sign")
						.addUsage("/sign <destination>  [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]")
						.addUsage("/s <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]")
						.addExample("/sign MyWorld -c random")
						.addExample("/sign MyWorld -c -100,65,254 -d south")
						.addExample("/sign MyWorld -d southeast")
						.addExample("/sign MyWorld -c -100,65,254")
						.addExample("/sign MyWorld");
				
				Help.register(sign);
			}
			
			getLog().info("Sign module activated");
		}
		if (modules.getNode("levers").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new LeverListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdLever, "lever", "l");

			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help lever = new Help("lever", "lever", "Use this command to create a lever that will teleport you to other worlds")
					    .setPermission("pjp.cmd.lever")
						.addUsage("/lever <destination>  [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]")
						.addUsage("/l <destination> [-b] [-c <x,y,z>] [-d <direction>] [-p <price>]")
						.addExample("/lever MyWorld -c random")
						.addExample("/lever MyWorld -c -100,65,254 -d south")
						.addExample("/lever MyWorld -d southeast")
						.addExample("/lever MyWorld -c -100,65,254")
						.addExample("/lever MyWorld");
				
				Help.register(lever);
			}
			
			getLog().info("Lever module activated");
		}
		if (modules.getNode("homes").getBoolean()) {
			Sponge.getDataManager().register(HomeData.class, ImmutableHomeData.class, new HomeData.Builder());
			Sponge.getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");

			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help homeCreate = new Help("home create", "create", "Create a new home")
						.setPermission("pjp.cmd.home.create")
						.addUsage("/home create <name>")
						.addUsage("/h c <name>")
						.addExample("/home create MyHome");
				
				Help homeList = new Help("home list", "list", "List all homes")
						.setPermission("pjp.cmd.home.list")
						.addUsage("/home list\n /h ls")
						.addUsage("/h ls");
				
				Help homeRemove = new Help("home remove", "remove", "Remove an existing home")
						.setPermission("pjp.cmd.home.remove")
						.addUsage("/home remove <name>")
						.addUsage("/h r <name>")
						.addExample("/home remove OldHome");
				
				Help homeRename = new Help("home rename", "rename", "Rename home")
						.setPermission("pjp.cmd.home.rename")
						.addUsage("/home rename <oldName> <newName>")
						.addUsage("/h rn <oldName> <newName>")
						.addExample("/home rename MyHome Castle");
				
				Help home = new Help("home", "home", " Top level home command")
						.setPermission("pjp.cmd.home")
						.addChild(homeRename)
						.addChild(homeRemove)
						.addChild(homeList)
						.addChild(homeCreate);
				
				Help.register(home);
			}
			
			getLog().info("Home module activated");
		}
		if (modules.getNode("warps").getBoolean()) {
			Sponge.getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "w");
			
			if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
				Help warpCreate = new Help("warp create", "create", "Use this command to create a warp that will teleport you to other worlds")
						.setPermission("pjp.cmd.warp.create")
						.addUsage("/warp create <name> [<destination> [-b] [-c <x,y,z>] [-d <direction>]] [-p <price>]")
						.addUsage("/w <name> [<destination> [-b] [-c <x,y,z>] [-d <direction>]] [-p <price>]")
						.addExample("/warp create Lobby MyWorld")
						.addExample("/warp create Lobby MyWorld -c -100,65,254")
						.addExample("/warp create Random MyWorld -c random")
						.addExample("/warp create Lobby MyWorld -c -100,65,254 -d south")
						.addExample("/warp create Lobby MyWorld -d southeast")
						.addExample("/warp create Lobby");
				
				Help warpList = new Help("warp list", "list", "List all warp points")
						.setPermission("pjp.cmd.warp.list")
						.addUsage("/warp list")
						.addUsage("/w ls");
				
				Help warpPrice = new Help("warp price", "price", "Charge players for using warps. 0 to disable")
						.setPermission("pjp.cmd.warp.price")
						.addUsage("/warp price <name> <price>")
						.addUsage("/w p <name> <price>")
						.addExample("/warp price Lobby 0")
						.addExample("/warp price Lobby 50");
				
				Help warpRemove = new Help("warp remove", "remove", "Remove an existing  warp point")
						.setPermission("pjp.cmd.warp.remove")
						.addUsage("/warp remove <name>")
						.addUsage("/w r <name>")
						.addExample("/warp remove OldSpawn");
				
				Help warpRename = new Help("warp rename", "rename", "Rename warp")
						.setPermission("pjp.cmd.warp.rename")
						.addUsage("/warp rename <oldName> <newName>")
						.addUsage("/w rn <oldName> <newName>")
						.addExample("/warp rename Spawn Lobby");
				
				Help warp = new Help("warp", "warp", " Top level warp command")
						.setPermission("pjp.cmd.warp")
						.addChild(warpRename)
						.addChild(warpRemove)
						.addChild(warpPrice)
						.addChild(warpList)
						.addChild(warpCreate);
				
				Help.register(warp);
			}
			
			getLog().info("Warp module activated");
		}

		SQLUtils.createTables();
	}

	@Listener
	public void onStartedServer(GameStartedServerEvent event) {
		Portal.init();
	}

	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		Sponge.getEventManager().unregisterPluginListeners(getPlugin());

		for (CommandMapping mapping : Sponge.getCommandManager().getOwnedBy(getPlugin())) {
			Sponge.getCommandManager().removeMapping(mapping);
		}

		ConfigManager configManager = ConfigManager.init();
		ConfigurationNode config = configManager.getConfig();

		Timings timings = new Timings();

		Sponge.getEventManager().registerListeners(this, new TeleportListener(timings));

		Sponge.getCommandManager().register(this, new CMDBack().cmdBack, "back");
		Sponge.getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");

		ConfigurationNode modules = config.getNode("settings", "modules");

		if (modules.getNode("portals").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new PortalListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");

			if (config.getNode("options", "portal", "legacy_builder").getBoolean()) {
				Sponge.getEventManager().registerListeners(this, new LegacyListener(timings));
			}

			getLog().info("Portal module activated");
		}
		if (modules.getNode("buttons").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new ButtonListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdButton, "button", "b");

			getLog().info("Button module activated");
		}
		if (modules.getNode("doors").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new DoorListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdDoor, "door", "d");

			getLog().info("Door module activated");
		}
		if (modules.getNode("plates").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new PlateListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdPlate, "plate", "pp");

			getLog().info("Pressure plate module activated");
		}
		if (modules.getNode("signs").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new SignListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdSign, "sign", "s");

			getLog().info("Sign module activated");
		}
		if (modules.getNode("levers").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new LeverListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdLever, "lever", "l");

			getLog().info("Lever module activated");
		}
		if (modules.getNode("homes").getBoolean()) {
			Sponge.getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");

			getLog().info("Home module activated");
		}
		if (modules.getNode("warps").getBoolean()) {
			Sponge.getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "w");

			getLog().info("Warp module activated");
		}

		Portal.init();
	}

	public Logger getLog() {
		return log;
	}

	public Path getPath() {
		return path;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static Main instance() {
		return instance;
	}

}