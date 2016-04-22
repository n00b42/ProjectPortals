package com.gmail.trentech.pjp.commands.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent.TargetPlayer;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.utils.Help;

public class CMDHome implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(args.hasAny("name")) {
			String homeName = args.<String>getOne("name").get().toLowerCase();

			HomeData homeData;

			Optional<HomeData> optionalHomeData = player.get(HomeData.class);
			
			if(optionalHomeData.isPresent()){
				homeData = optionalHomeData.get();
			}else{
				homeData = new HomeData();
			}

			Optional<Location<World>> optionalSpawnLocation = homeData.getDestination(homeName);
			
			if(!optionalSpawnLocation.isPresent()){
				src.sendMessage(Text.of(TextColors.DARK_RED, homeName, " does not exist or is invalid"));
				return CommandResult.empty();
			}			
			Location<World> spawnLocation = optionalSpawnLocation.get();

			if(args.hasAny("player")) {
				String playerName = args.<String>getOne("player").get();

				if(!src.hasPermission("pjp.cmd.home.others")) {
					player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp others"));
					return CommandResult.empty();
				}
				
				Optional<Player> optionalPlayer = Main.getGame().getServer().getPlayer(playerName);
				
				if(!optionalPlayer.isPresent()){
					player.sendMessage(Text.of(TextColors.DARK_RED, playerName, " does not exist"));
					return CommandResult.empty();
				}
				
				player = optionalPlayer.get();
			}

			TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), spawnLocation, 0, Cause.of(NamedCause.source("home")));

			if(!Main.getGame().getEventManager().post(teleportEvent)){
				Location<World> currentLocation = player.getLocation();
				spawnLocation = teleportEvent.getDestination();
				
				Optional<Vector3d> optionalRotation = homeData.getRotation(homeName);
				
				if(optionalRotation.isPresent()){
					player.setLocationAndRotation(spawnLocation, optionalRotation.get());
				}else{
					player.setLocation(spawnLocation);
				}
				
				TargetPlayer displaceEvent = SpongeEventFactory.createDisplaceEntityEventTargetPlayer(Cause.of(NamedCause.source(this)), new Transform<World>(currentLocation), new Transform<World>(spawnLocation), player);
				Main.getGame().getEventManager().post(displaceEvent);
			}

			return CommandResult.success();
		}

		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		if(src.hasPermission("pjp.cmd.home.others")) {
			list.add(Text.of(TextColors.YELLOW, " /home <name> [player]\n"));
		}
		if(src.hasPermission("pjp.cmd.home.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("hcreate"))).append(Text.of(" /home create")).build());
		}
		if(src.hasPermission("pjp.cmd.home.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("hremove"))).append(Text.of(" /home remove")).build());
		}
		if(src.hasPermission("pjp.cmd.home.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("hlist"))).append(Text.of(" /home list")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
