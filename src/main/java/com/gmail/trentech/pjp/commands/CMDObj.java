package com.gmail.trentech.pjp.commands;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.Help;

public class CMDObj {

	public static class Button extends CMDObjBase {

		public Button() {
			super("button");

			Help help = new Help("button", "button", " Use this command to create a button that will teleport you to other worlds");
			help.setPermission("pjp.cmd.button");
			help.setSyntax(" /button <destination>  [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]\n /b <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]");
			help.setExample(" /button MyWorld\n /button MyWorld -c -100,65,254\n /button MyWorld -c random\n /button MyWorld -c -100,65,254 -d south\n /button MyWorld -d southeast\n /button MyWorld -p 50");
			help.save();
		}

		@Override
		protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation, double price) {
			if(server.isPresent()) {
				ButtonListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.BUTTON, server.get(), rotation, price));
			} else {
				ButtonListener.builders.put(player.getUniqueId(), new Portal.Local(PortalType.BUTTON, world.get(), vector3d, rotation, price));
			}
		}
	}

	public static class Door extends CMDObjBase {

		public Door() {
			super("door");

			Help help = new Help("door", "door", " Use this command to create a door that will teleport you to other worlds");
			help.setPermission("pjp.cmd.door");
			help.setSyntax(" /door <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]\n /d <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]");
			help.setExample(" /door MyWorld\n /door MyWorld -c -100,65,254\n /door MyWorld -c random\n /door MyWorld -c -100,65,254 -d south\n /door MyWorld -d southeast\n /door MyWorld -p 50");
			help.save();
		}

		@Override
		protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation, double price) {
			if(server.isPresent()) {
				DoorListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.DOOR, server.get(), rotation, price));
			} else {
				DoorListener.builders.put(player.getUniqueId(), new Portal.Local(PortalType.DOOR, world.get(), vector3d, rotation, price));
			}
		}
	}

	public static class Lever extends CMDObjBase {

		public Lever() {
			super("lever");

			Help help = new Help("lever", "lever", " Use this command to create a lever that will teleport you to other worlds");
			help.setPermission("pjp.cmd.lever");
			help.setSyntax(" /lever <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]\n /l <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]");
			help.setExample(" /lever MyWorld\n /lever MyWorld -c -100,65,254\n /lever MyWorld -c random\n /lever MyWorld -c -100,65,254 -d south\n /lever MyWorld -d southeast\n /lever MyWorld -p 50");
			help.save();
		}

		@Override
		protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation, double price) {
			if(server.isPresent()) {
				LeverListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.LEVER, server.get(), rotation, price));
			} else {
				LeverListener.builders.put(player.getUniqueId(), new Portal.Local(PortalType.LEVER, world.get(), vector3d, rotation, price));
			}
		}
	}

	public static class Plate extends CMDObjBase {

		public Plate() {
			super("pressure plate");

			Help help = new Help("plate", "plate", " Use this command to create a pressure plate that will teleport you to other worlds");
			help.setPermission("pjp.cmd.plate");
			help.setSyntax(" /plate <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]\n /pp <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]");
			help.setExample(" /plate MyWorld\n /plate MyWorld -c -100,65,254\n /plate MyWorld -c random\n /plate MyWorld -c -100,65,254 -d south\n /plate MyWorld -d southeast\n /plate MyWorld -p 50");
			help.save();
		}

		@Override
		protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation, double price) {
			if(server.isPresent()) {
				PlateListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.PLATE, server.get(), rotation, price));
			} else {
				PlateListener.builders.put(player.getUniqueId(), new Portal.Local(PortalType.PLATE, world.get(), vector3d, rotation, price));
			}
		}
	}

	public static class Sign extends CMDObjBase {

		public Sign() {
			super("sign");

			Help help = new Help("sign", "sign", " Use this command to create a sign that will teleport you to other worlds");
			help.setPermission("pjp.cmd.sign");
			help.setSyntax(" /sign <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]\n /s <destination> [-b] [-c <x,y,z>] [-d <rotation>] [-p <price>]");
			help.setExample(" /sign MyWorld\n /sign MyWorld -c -100,65,254\n /sign MyWorld -c random\n /sign MyWorld -c -100,65,254 -d south\n /sign MyWorld -d southeast\n /sign MyWorld -p 50");
			help.save();
		}

		@Override
		protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation, double price) {
			if(server.isPresent()) {
				SignListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.SIGN, server.get(), rotation, price));
			} else {
				SignListener.builders.put(player.getUniqueId(), new Portal.Local(PortalType.SIGN, world.get(), vector3d, rotation, price));
			}
		}
	}
}