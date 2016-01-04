package com.gmail.trentech.pjp.commands.warp;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.TeleportEvent;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDWarp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(args.hasAny("name")) {
			String warpName = args.<String>getOne("name").get();
			
			ConfigurationNode config = new ConfigManager("warps.conf").getConfig();

			if(config.getNode("Warps", warpName).getString() == null){
				src.sendMessage(Text.of(TextColors.DARK_RED, warpName, " does not exist"));
				return CommandResult.empty();
			}
			
			if(!player.hasPermission("pjp.warps." + warpName)){
				player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp here"));
				return CommandResult.empty();
			}
			
			String worldName = config.getNode("Warps", warpName, "World").getString();
			
			if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
				player.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
				return CommandResult.empty();
			}
			World world = Main.getGame().getServer().getWorld(worldName).get();
			
			int x = config.getNode("Warps", warpName, "X").getInt();
			int y = config.getNode("Warps", warpName, "Y").getInt();
			int z = config.getNode("Warps", warpName, "Z").getInt();

			if(args.hasAny("player")) {
				String playerName = args.<String>getOne("player").get();
				
				if(!src.hasPermission("pjp.cmd.warp.others")) {
					player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to warp others"));
					return CommandResult.empty();
				}

				if(!Main.getGame().getServer().getPlayer(playerName).isPresent()){
					player.sendMessage(Text.of(TextColors.DARK_RED, playerName, " does not exist"));
					return CommandResult.empty();
				}
				
				player = Main.getGame().getServer().getPlayer(playerName).get();
			}
			
			Main.getGame().getEventManager().post(new TeleportEvent(player.getLocation(), world.getLocation(x, y, z), Cause.of(player)));
			
			return CommandResult.success();
		}

		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		if(src.hasPermission("pjp.cmd.warp.others")) {
			list.add(Text.of(TextColors.GREEN, " /warp <name> [player]"));
		}
		if(src.hasPermission("pjp.cmd.warp.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/warp help Create")).append(Text.of(" /warp create")).build());
		}
		if(src.hasPermission("pjp.cmd.warp.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/warp help Remove")).append(Text.of(" /warp remove")).build());
		}
		if(src.hasPermission("pjp.cmd.warp.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/warp help List")).append(Text.of(" /warp list")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
