package com.gmail.trentech.pjp.data;

import java.util.Map;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.pjp.portal.Portal;
import com.google.common.reflect.TypeToken;

public class Keys {

	private static final TypeToken<Map<String, Portal>> PORTALS_MAP = new TypeToken<Map<String, Portal>>() {
		private static final long serialVersionUID = 2375752428409666167L;
	};
	private static final TypeToken<MapValue<String, Portal>> PORTALS_MAP_VALUE = new TypeToken<MapValue<String, Portal>>() {
		private static final long serialVersionUID = -6254940587461711335L;
	};
	private static final TypeToken<Value<Portal>> PORTAL_VALUE = new TypeToken<Value<Portal>>() {
		private static final long serialVersionUID = 395242399877312340L;
	};
	private static final TypeToken<Portal> PORTAL_TOKEN = new TypeToken<Portal>() {
		private static final long serialVersionUID = -8726734755833911770L;
	};

	public static final Key<Value<Portal>> PORTAL = KeyFactory.makeSingleKey(PORTAL_TOKEN, PORTAL_VALUE, DataQuery.of("portal"), "pjp:portal", "portal");
	public static final Key<MapValue<String, Portal>> PORTALS = KeyFactory.makeMapKey(PORTALS_MAP, PORTALS_MAP_VALUE, DataQuery.of("portals"), "pjp:portals", "portals");
}
