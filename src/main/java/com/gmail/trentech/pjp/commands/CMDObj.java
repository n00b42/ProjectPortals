package com.gmail.trentech.pjp.commands;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.Local;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.portal.Portal.Server;
import com.gmail.trentech.pjp.portal.features.Command;
import com.gmail.trentech.pjp.portal.features.Coordinate;
import com.gmail.trentech.pjp.rotation.Rotation;

public class CMDObj {

	public static class Button extends CMDObjBase {

		public Button() {
			super("button");
		}

		@Override
		protected void init(Player player, Rotation rotation, double price, boolean force, Optional<String> server, Optional<Coordinate> coordinate, Optional<String> permission, Optional<Command> command) {
			if (server.isPresent()) {
				Server portal = new Portal.Server(PortalType.BUTTON, server.get(), rotation, price);
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				ButtonListener.builders.put(player.getUniqueId(), portal);
			} else {
				Local portal = new Portal.Local(PortalType.BUTTON, rotation, price, force);
				
				if(coordinate.isPresent()) {
					portal.setCoordinate(coordinate.get());
				}
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				ButtonListener.builders.put(player.getUniqueId(), portal);
			}
		}
	}

	public static class Door extends CMDObjBase {

		public Door() {
			super("door");
		}

		@Override
		protected void init(Player player, Rotation rotation, double price, boolean force, Optional<String> server, Optional<Coordinate> coordinate, Optional<String> permission, Optional<Command> command) {
			if (server.isPresent()) {
				Server portal = new Portal.Server(PortalType.DOOR, server.get(), rotation, price);
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				DoorListener.builders.put(player.getUniqueId(), portal);
			} else {
				Local portal = new Portal.Local(PortalType.DOOR, rotation, price, force);
				
				if(coordinate.isPresent()) {
					portal.setCoordinate(coordinate.get());
				}
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				DoorListener.builders.put(player.getUniqueId(), portal);
			}
		}
	}

	public static class Lever extends CMDObjBase {

		public Lever() {
			super("lever");
		}

		@Override
		protected void init(Player player, Rotation rotation, double price, boolean force, Optional<String> server, Optional<Coordinate> coordinate, Optional<String> permission, Optional<Command> command) {
			if (server.isPresent()) {
				Server portal = new Portal.Server(PortalType.LEVER, server.get(), rotation, price);
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				LeverListener.builders.put(player.getUniqueId(), portal);
			} else {
				Local portal = new Portal.Local(PortalType.LEVER, rotation, price, force);
				
				if(coordinate.isPresent()) {
					portal.setCoordinate(coordinate.get());
				}
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				LeverListener.builders.put(player.getUniqueId(), portal);
			}
		}
	}

	public static class Plate extends CMDObjBase {

		public Plate() {
			super("pressure plate");
		}

		@Override
		protected void init(Player player, Rotation rotation, double price, boolean force, Optional<String> server, Optional<Coordinate> coordinate, Optional<String> permission, Optional<Command> command) {
			if (server.isPresent()) {
				Server portal = new Portal.Server(PortalType.PLATE, server.get(), rotation, price);
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				PlateListener.builders.put(player.getUniqueId(), portal);
			} else {
				Local portal = new Portal.Local(PortalType.PLATE, rotation, price, force);
				
				if(coordinate.isPresent()) {
					portal.setCoordinate(coordinate.get());
				}
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				PlateListener.builders.put(player.getUniqueId(), portal);
			}
		}
	}

	public static class Sign extends CMDObjBase {

		public Sign() {
			super("sign");
		}

		@Override
		protected void init(Player player, Rotation rotation, double price, boolean force, Optional<String> server, Optional<Coordinate> coordinate, Optional<String> permission, Optional<Command> command) {
			if (server.isPresent()) {
				Server portal = new Portal.Server(PortalType.SIGN, server.get(), rotation, price);
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				SignListener.builders.put(player.getUniqueId(), portal);
			} else {
				Local portal = new Portal.Local(PortalType.SIGN, rotation, price, force);
				
				if(coordinate.isPresent()) {
					portal.setCoordinate(coordinate.get());
				}
				
				if(permission.isPresent()) {
					portal.setPermission(permission.get());
				}
				
				if(command.isPresent()) {
					portal.setCommand(command.get());
				}
				
				SignListener.builders.put(player.getUniqueId(), portal);
			}
		}
	}
}