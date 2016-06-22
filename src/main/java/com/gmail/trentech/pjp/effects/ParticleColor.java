package com.gmail.trentech.pjp.effects;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.api.util.Color;

public enum ParticleColor {

	BLACK("BLACK", Color.BLACK), 
	BLUE("BLUE", Color.BLUE), 
	CYAN("CYAN", Color.CYAN), 
	DARK_CYAN("DARK_CYAN", Color.DARK_CYAN), 
	DARK_GREEN("DARK_GREEN", Color.DARK_GREEN), 
	DARK_MAGENTA("DARK_MAGENTA", Color.DARK_MAGENTA), 
	GRAY("GRAY", Color.GRAY), 
	GREEN("GREEN", Color.GREEN), 
	LIME("LIME", Color.LIME), 
	MAGENTA("MAGENTA", Color.MAGENTA), 
	NAVY("NAVY", Color.NAVY), 
	PINK("PINK", Color.PINK), 
	PURPLE("PURPLE", Color.PURPLE), 
	RED("RED", Color.RED), 
	WHITE("WHITE", Color.WHITE), 
	YELLOW("YELLOW", Color.YELLOW), 
	RAINBOW("RAINBOW", null);

	private final Color color;
	private final String name;

	private ParticleColor(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return this.name;
	}

	public Color getColor() {
		if (this.color == null) {
			int random = ThreadLocalRandom.current().nextInt(8 - 1 + 1) + 1;

			switch (random) {
			case 1:
				return Color.BLUE;
			case 2:
				return Color.CYAN;
			case 3:
				return Color.LIME;
			case 4:
				return Color.MAGENTA;
			case 5:
				return Color.PINK;
			case 6:
				return Color.PURPLE;
			case 7:
				return Color.RED;
			case 8:
				return Color.YELLOW;
			}
		}
		return color;
	}

	public static Optional<ParticleColor> get(String name) {
		Optional<ParticleColor> optional = Optional.empty();

		ParticleColor[] colors = ParticleColor.values();

		for (ParticleColor color : colors) {
			if (color.name.equals(name)) {
				optional = Optional.of(color);
				break;
			}
		}

		return optional;
	}
}
