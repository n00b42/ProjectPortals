package com.gmail.trentech.pjp.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.pjp.commands.home.CMDHome;
import com.gmail.trentech.pjp.commands.portal.CMDPortal;
import com.gmail.trentech.pjp.commands.portal.CMDSave;
import com.gmail.trentech.pjp.commands.warp.CMDWarp;

public class CommandManager {
	
	public CommandSpec cmdWarpCreate = CommandSpec.builder()
		    .description(Text.of("Create a new warp point"))
		    .permission("pjp.cmd.warp.create")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("destination"))), GenericArguments.flags()
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
    				.valueFlag(GenericArguments.string(Text.of("direction")), "d")
    				.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none()))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDCreate())
		    .build();
	
	public CommandSpec cmdWarpRemove = CommandSpec.builder()
		    .description(Text.of("Remove an existing warp point"))
		    .permission("pjp.cmd.warp.remove")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDRemove())
		    .build();
	
	public CommandSpec cmdWarpPrice = CommandSpec.builder()
		    .description(Text.of("Remove an existing warp point"))
		    .permission("pjp.cmd.warp.price")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("price"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDPrice())
		    .build();
	
	public CommandSpec cmdWarpList = CommandSpec.builder()
		    .description(Text.of("List all available warp points"))
		    .permission("pjp.cmd.warp.list")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.warp.CMDList())
		    .build();
	
	public CommandSpec cmdWarp = CommandSpec.builder()
		    .description(Text.of("Warp base command"))
		    .permission("pjp.cmd.warp")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("player"))))
		    .child(cmdWarpCreate, "create", "c")
		    .child(cmdWarpRemove, "remove", "r")
		    .child(cmdWarpList, "list", "l")
		    .child(cmdWarpPrice, "price", "p")
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

	public CommandSpec cmdHome = CommandSpec.builder()
		    .description(Text.of("Home base command"))
		    .permission("pjp.cmd.home")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("player"))))
		    .child(cmdHomeCreate, "create", "c")
		    .child(cmdHomeRemove, "remove", "r")
		    .child(cmdHomeList, "list", "l")
		    .executor(new CMDHome())
		    .build();

	public CommandSpec cmdPortalCreate = CommandSpec.builder()
		    .description(Text.of("Create a new portal"))
		    .permission("pjp.cmd.portal.create")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.flags()
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
    				.valueFlag(GenericArguments.string(Text.of("direction")), "d")
    				.valueFlag(GenericArguments.string(Text.of("price")), "p")
		    		.valueFlag(GenericArguments.string(Text.of("particle[:color]")), "e").buildWith(GenericArguments.none()))
		    .executor(new com.gmail.trentech.pjp.commands.portal.CMDCreate())
		    .build();

	public CommandSpec cmdPortalRemove = CommandSpec.builder()
		    .description(Text.of("Remove an portal"))
		    .permission("pjp.cmd.portal.remove")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.portal.CMDRemove())
		    .build();
	
	public CommandSpec cmdPortalParticle = CommandSpec.builder()
		    .description(Text.of("Change portal particles"))
		    .permission("pjp.cmd.portal.particle")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("type")))
		    		, GenericArguments.optional(GenericArguments.string(Text.of("color"))))
		    .executor(new com.gmail.trentech.pjp.commands.portal.CMDParticle())
		    .build();
	
	public CommandSpec cmdPortalPrice = CommandSpec.builder()
		    .description(Text.of("Change portal price"))
		    .permission("pjp.cmd.portal.price")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("price"))))
		    .executor(new com.gmail.trentech.pjp.commands.portal.CMDPrice())
		    .build();
	
	public CommandSpec cmdPortalList = CommandSpec.builder()
		    .description(Text.of("List all portals"))
		    .permission("pjp.cmd.portal.list")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new com.gmail.trentech.pjp.commands.portal.CMDList())
		    .build();

	public CommandSpec cmdSave = CommandSpec.builder()
		    .description(Text.of("Saves generated portal"))
		    .permission("pjp.cmd.portal.create")
		    .executor(new CMDSave())
		    .build();
	
	public CommandSpec cmdPortal = CommandSpec.builder()
		    .description(Text.of("Portal base command"))
		    .permission("pjp.cmd.portal")
		    .child(cmdPortalCreate, "create", "c")
		    .child(cmdPortalRemove, "remove", "r")
		    .child(cmdPortalParticle, "particle", "p")
		    .child(cmdPortalPrice, "price", "pr")
		    .child(cmdPortalList, "list", "l")
		    .child(cmdSave, "save", "s")
		    .executor(new CMDPortal())
		    .build();
	
	
	public CommandSpec cmdButton = CommandSpec.builder()
		    .description(Text.of("Button base command"))
		    .permission("pjp.cmd.button")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.flags()		    				
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
		    		.valueFlag(GenericArguments.string(Text.of("direction")), "d")
		    		.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none()))
		    .executor(new CMDButton())
		    .build();

	public CommandSpec cmdDoor = CommandSpec.builder()
		    .description(Text.of("Door base command"))
		    .permission("pjp.cmd.door")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.flags()
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
		    		.valueFlag(GenericArguments.string(Text.of("direction")), "d")
		    		.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none()))
		    .executor(new CMDDoor())
		    .build();
	
	public CommandSpec cmdLever = CommandSpec.builder()
		    .description(Text.of("Lever base command"))
		    .permission("pjp.cmd.lever")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.flags()
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
		    		.valueFlag(GenericArguments.string(Text.of("direction")), "d")
		    		.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none()))
		    .executor(new CMDLever())
		    .build();
	
	public CommandSpec cmdPlate = CommandSpec.builder()
		    .description(Text.of("Plate base command"))
		    .permission("pjp.cmd.plate")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.flags()
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
		    		.valueFlag(GenericArguments.string(Text.of("direction")), "d")
		    		.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none()))
		    .executor(new CMDPlate())
		    .build();

	public CommandSpec cmdSign = CommandSpec.builder()
		    .description(Text.of("Sign base command"))
		    .permission("pjp.cmd.sign")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.flags()
		    		.flag("b")
		    		.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
		    		.valueFlag(GenericArguments.string(Text.of("direction")), "d")
		    		.valueFlag(GenericArguments.string(Text.of("price")), "p").buildWith(GenericArguments.none()))
		    .executor(new CMDSign())
		    .build();
	
	public CommandSpec cmdPJP = CommandSpec.builder()
		    .description(Text.of("Lists all Project Worlds commands"))
		    .permission("pjp.cmd")
		    .executor(new CMDPjp())
		    .build();
}
