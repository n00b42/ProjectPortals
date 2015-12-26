package com.gmail.trentech.pjp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDShow implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}

		String worldName = null;
		if(args.hasAny("name")) {
			worldName = args.<String>getOne("name").get();
		}

		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();

		List<String> locationNames = new ArrayList<>();

		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Cuboids").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			locationNames.addAll(config.getNode("Cuboids", uuid, "Locations").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()));
		}

		List<Location<World>> locations = new ArrayList<>();
		
		for(String node : locationNames){
			String[] split = node.split("\\.");
			
			if(worldName != null){
				if(!worldName.equalsIgnoreCase(split[0])){
					continue;
				}
			}

			if(!Main.getGame().getServer().getWorld(split[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(split[0]).get();

			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			int z = Integer.parseInt(split[3]);

			Location<World> location = world.getLocation(x, y, z);
			
			locations.add(location);
		}
		
		for(Location<World> location : locations){
			location.getExtent().spawnParticles(ParticleEffect.builder()
					.type(ParticleTypes.BARRIER).count(1).build(), location.getPosition().add(.5,.5,.5));
		}

		return CommandResult.success();
	}
}
