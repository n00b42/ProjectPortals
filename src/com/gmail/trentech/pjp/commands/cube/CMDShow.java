package com.gmail.trentech.pjp.commands.cube;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.portals.Cuboid;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;

public class CMDShow implements CommandExecutor {

	public CMDShow(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "cube").getString();
		
		Help help = new Help("show", " Fills all portal regions to make them temporarly visible");
		help.setSyntax(" /cube show\n /" + alias + " s");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}

		for(Cuboid cube : Cuboid.list()){
			for(Location<World> location : cube.getRegion()){
				location.getExtent().spawnParticles(ParticleEffect.builder()
						.type(ParticleTypes.BARRIER).count(1).build(), location.getPosition().add(.5,.5,.5));
			}
		}

		return CommandResult.success();
	}
}
