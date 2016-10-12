package com.gmail.trentech.pjp.portal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;

import com.gmail.trentech.pjp.portal.Portal.Local;
import com.gmail.trentech.pjp.portal.Portal.Server;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class Serializer {

	public static String serialize(Portal portal) {
		DataContainer container;

		if (portal instanceof Server) {
			container = ((Server) portal).toContainer();
		} else {
			container = ((Local) portal).toContainer();
		}

		ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(container);

		StringWriter stringWriter = new StringWriter();
		try {
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	public static Portal deserialize(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

		Optional<Local> optional = Sponge.getDataManager().deserialize(Local.class, dataView);

		if (optional.isPresent()) {
			return optional.get();
		} else {
			return Sponge.getDataManager().deserialize(Server.class, dataView).get();
		}
	}
}
