package com.gmail.trentech.pjp.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {
	
	public CommandSpec cmdTeleportUnSafe = CommandSpec.builder()
		    .executor(new CMDTeleportUnSafe())
		    .build();
	
	public CommandSpec cmdShow = CommandSpec.builder()
		    .description(Text.of("Fills all portal regions to make them temporarly visible"))
		    .permission("pjp.cmd.portal.cube.show")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name"))))
		    .executor(new CMDShow())
		    .build();
	
	public CommandSpec cmdCube = CommandSpec.builder()
		    .description(Text.of("Create cuboid portal"))
		    .permission("pjw.cmd.portal.cube")
		    .child(cmdShow, "show", "s")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))),
		    		GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("coords"))))
		    .executor(new CMDCube())
		    .build();
	
	public CommandSpec cmdButton = CommandSpec.builder()
		    .description(Text.of("Place a button portal"))
		    .permission("pjw.cmd.portal.button")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))),
		    		GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("coords"))))
		    .executor(new CMDButton())
		    .build();
	
	public CommandSpec cmdPlate = CommandSpec.builder()
		    .description(Text.of("Place a pressure plate portal"))
		    .permission("pjp.cmd.portal.plate")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))),
		    		GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("coords"))))
		    .executor(new CMDPlate())
		    .build();

	public CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Text.of("I need help"))
		    .permission("pjp.cmd.portal")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("command"))))
		    .executor(new CMDHelp())
		    .build();
	
	public CommandSpec cmdPortal = CommandSpec.builder()
		    .description(Text.of("Portal base command"))
		    .permission("pjp.cmd.portal")
		    .child(cmdCube, "cube", "c")
		    .child(cmdButton, "button", "b")
		    .child(cmdPlate, "plate", "p")
		    .child(cmdHelp, "help", "h")
		    .executor(new CMDPortal())
		    .build();
}
