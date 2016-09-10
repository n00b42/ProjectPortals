package com.gmail.trentech.pjp.commands.portal;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.data.object.Portal;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.utils.Help;

public class CMDParticle implements CommandExecutor {

	public CMDParticle() {
		Help help = new Help("particle", "particle", " change a portals particle effect. Color currently only available for REDSTONE");
		help.setSyntax(" /portal particle <name> <type> [color]\n /p p <name> <type> [color]");
		help.setExample(" /portal particle MyPortal CRIT\n /portal particle MyPortal REDSTONE BLUE");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String> getOne("name").get().toLowerCase();

		if (!Portal.get(name).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"));
		}
		Portal portal = Portal.get(name).get();

		Particle particle = args.<Particles> getOne("type").get().getParticle();

		Optional<ParticleColor> color = Optional.empty();

		if (args.hasAny("color")) {
			if (particle.isColorable()) {
				color = Optional.of(args.<ParticleColor> getOne("color").get());
			} else {
				src.sendMessage(Text.of(TextColors.YELLOW, "Colors currently only works with REDSTONE type"));
			}
		}

		portal.setParticle(particle);
		portal.setParticleColor(color);
		portal.update();

		return CommandResult.success();
	}

}
