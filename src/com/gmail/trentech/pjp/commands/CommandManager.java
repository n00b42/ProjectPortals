package com.gmail.trentech.pjp.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.pjp.commands.home.CMDHome;
import com.gmail.trentech.pjp.commands.portal.CMDButton;
import com.gmail.trentech.pjp.commands.portal.CMDCube;
import com.gmail.trentech.pjp.commands.portal.CMDHelp;
import com.gmail.trentech.pjp.commands.portal.CMDPlate;
import com.gmail.trentech.pjp.commands.portal.CMDPortal;
import com.gmail.trentech.pjp.commands.portal.CMDShow;
import com.gmail.trentech.pjp.commands.warp.CMDWarp;

public class CommandManager {
	
	public CommandSpec cmdTeleportUnSafe = CommandSpec.builder()
		    .executor(new CMDTeleportUnSafe())
		    .build();
	
	
	public CommandSpec cmdWarpCreate = CommandSpec.builder()
		    .description(Text.of("Create a new warp point"))
		    .permission("pjp.cmd.warp.create")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDCreate())
		    .build();
	
	public CommandSpec cmdWarpRemove = CommandSpec.builder()
		    .description(Text.of("Remove an existing warp point"))
		    .permission("pjp.cmd.warp.remove")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDRemove())
		    .build();
	
	public CommandSpec cmdWarpList = CommandSpec.builder()
		    .description(Text.of("List all available warp points"))
		    .permission("pjp.cmd.warp.list")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDList())
		    .build();
	
	public CommandSpec cmdWarpHelp = CommandSpec.builder()
		    .description(Text.of("I need help"))
		    .permission("pjp.cmd.warp")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDHelp())
		    .build();
	
	public CommandSpec cmdWarp = CommandSpec.builder()
		    .description(Text.of("Warp base command"))
		    .permission("pjp.cmd.warp")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))),
		    		GenericArguments.optional(GenericArguments.string(Text.of("player"))))
		    .child(cmdWarpCreate, "create", "c")
		    .child(cmdWarpRemove, "remove", "r")
		    .child(cmdWarpList, "list", "l")
		    .child(cmdWarpHelp, "help", "h")
		    .executor(new CMDWarp())
		    .build();
	
	
	public CommandSpec cmdHomeCreate = CommandSpec.builder()
		    .description(Text.of("Create a new home"))
		    .permission("pjp.cmd.home.create")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.home.CMDCreate())
		    .build();
	
	public CommandSpec cmdHomeRemove = CommandSpec.builder()
		    .description(Text.of("Remove an existing home"))
		    .permission("pjp.cmd.home.remove")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.home.CMDRemove())
		    .build();
	
	public CommandSpec cmdHomeList = CommandSpec.builder()
		    .description(Text.of("List all homes"))
		    .permission("pjp.cmd.home.list")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.home.CMDList())
		    .build();
	
	public CommandSpec cmdHomeHelp = CommandSpec.builder()
		    .description(Text.of("I need help"))
		    .permission("pjp.cmd.home")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.home.CMDHelp())
		    .build();
	
	public CommandSpec cmdHome = CommandSpec.builder()
		    .description(Text.of("Home base command"))
		    .permission("pjp.cmd.home")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))),
		    		GenericArguments.optional(GenericArguments.string(Text.of("player"))))
		    .child(cmdHomeCreate, "create", "c")
		    .child(cmdHomeRemove, "remove", "r")
		    .child(cmdHomeList, "list", "l")
		    .child(cmdHomeHelp, "help", "h")
		    .executor(new CMDHome())
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
