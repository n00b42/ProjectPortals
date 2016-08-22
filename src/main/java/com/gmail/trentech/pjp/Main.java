package com.gmail.trentech.pjp;

import org.slf4j.Logger;
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
import com.gmail.trentech.pjp.data.builder.data.ButtonBuilder;
import com.gmail.trentech.pjp.data.builder.data.DoorBuilder;
import com.gmail.trentech.pjp.data.builder.data.HomeBuilder;
import com.gmail.trentech.pjp.data.builder.data.PlateBuilder;
import com.gmail.trentech.pjp.data.builder.data.PortalBuilder;
import com.gmail.trentech.pjp.data.builder.data.SignBuilder;
import com.gmail.trentech.pjp.data.builder.data.WarpBuilder;
import com.gmail.trentech.pjp.data.builder.manipulator.HomeDataManipulatorBuilder;
import com.gmail.trentech.pjp.data.builder.manipulator.SignPortalDataManipulatorBuilder;
import com.gmail.trentech.pjp.data.immutable.ImmutableHomeData;
import com.gmail.trentech.pjp.data.immutable.ImmutableSignPortalData;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.mutable.SignPortalData;
import com.gmail.trentech.pjp.data.object.Button;
import com.gmail.trentech.pjp.data.object.Door;
import com.gmail.trentech.pjp.data.object.Home;
import com.gmail.trentech.pjp.data.object.Lever;
import com.gmail.trentech.pjp.data.object.Plate;
import com.gmail.trentech.pjp.data.object.Portal;
import com.gmail.trentech.pjp.data.object.Sign;
import com.gmail.trentech.pjp.data.object.Warp;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.HomeListener;
import com.gmail.trentech.pjp.listeners.LegacyListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.listeners.TeleportListener;
import com.gmail.trentech.pjp.listeners.Timings;
import com.gmail.trentech.pjp.listeners.WarpListener;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;
import com.gmail.trentech.pjp.utils.SQLUtils;

import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	private static Logger log;
	private static PluginContainer plugin;
	private static boolean legacy;

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		ConfigManager configManager = new ConfigManager();
		configManager.init();

		legacy = configManager.getConfig().getNode("options", "portal", "legacy_builder").getBoolean();

		Timings timings = new Timings();

		Sponge.getEventManager().registerListeners(this, new TeleportListener(timings));

		Sponge.getCommandManager().register(this, new CMDBack().cmdBack, "back");
		Sponge.getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");

		ConfigurationNode modules = configManager.getConfig().getNode("settings", "modules");

		if (modules.getNode("portals").getBoolean()) {
			Sponge.getDataManager().registerBuilder(Portal.class, new PortalBuilder());
			Sponge.getEventManager().registerListeners(this, new PortalListener(timings));

			if (isLegacy()) {
				Sponge.getEventManager().registerListeners(this, new LegacyListener(timings));
			}

			Sponge.getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");
			getLog().info("Portal module activated");
		}
		if (modules.getNode("buttons").getBoolean()) {
			Sponge.getDataManager().registerBuilder(Button.class, new ButtonBuilder());
			Sponge.getEventManager().registerListeners(this, new ButtonListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdButton, "button", "b");
			getLog().info("Button module activated");
		}
		if (modules.getNode("doors").getBoolean()) {
			Sponge.getDataManager().registerBuilder(Door.class, new DoorBuilder());
			Sponge.getEventManager().registerListeners(this, new DoorListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdDoor, "door", "d");
			getLog().info("Door module activated");
		}
		if (modules.getNode("plates").getBoolean()) {
			Sponge.getDataManager().registerBuilder(Plate.class, new PlateBuilder());
			Sponge.getEventManager().registerListeners(this, new PlateListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdPlate, "plate", "pp");
			getLog().info("Pressure plate module activated");
		}
		if (modules.getNode("signs").getBoolean()) {
			Sponge.getDataManager().register(SignPortalData.class, ImmutableSignPortalData.class, new SignPortalDataManipulatorBuilder());
			Sponge.getDataManager().registerBuilder(Sign.class, new SignBuilder());
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
			Sponge.getEventManager().registerListeners(this, new HomeListener());
			Sponge.getDataManager().register(HomeData.class, ImmutableHomeData.class, new HomeDataManipulatorBuilder());
			Sponge.getDataManager().registerBuilder(Home.class, new HomeBuilder());
			Sponge.getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");
			getLog().info("Home module activated");
		}
		if (modules.getNode("warps").getBoolean()) {
			Sponge.getEventManager().registerListeners(this, new WarpListener());
			Sponge.getDataManager().registerBuilder(Warp.class, new WarpBuilder());
			Sponge.getEventManager().registerListeners(this, new SignListener(timings));
			Sponge.getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "w");
			getLog().info("Warp module activated");
		}

		SQLUtils.createTables(modules);
	}

	@Listener
	public void onStartedServer(GameStartedServerEvent event) {
		ConfigurationNode modules = new ConfigManager().getConfig().getNode("settings", "modules");

		if (modules.getNode("portals").getBoolean()) {
			Portal.init();
		}
		if (modules.getNode("buttons").getBoolean()) {
			Button.init();
		}
		if (modules.getNode("doors").getBoolean()) {
			Door.init();
		}
		if (modules.getNode("plates").getBoolean()) {
			Plate.init();
		}
		if (modules.getNode("levers").getBoolean()) {
			Lever.init();
		}
		if (modules.getNode("warps").getBoolean()) {
			Warp.init();
		}
	}

	public static Logger getLog() {
		return log;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static boolean isLegacy() {
		return legacy;
	}
}