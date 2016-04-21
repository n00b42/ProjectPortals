package com.gmail.trentech.pjp.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

public class PJPKeys {

	public static final Key<Value<String>> PORTAL_NAME = KeyFactory.makeSingleKey(String.class, Value.class, DataQuery.of("name"));
	public static final Key<Value<String>> DESTINATION = KeyFactory.makeSingleKey(String.class, Value.class, DataQuery.of("destination"));
	public static final Key<Value<Double>> PRICE = KeyFactory.makeSingleKey(Double.class, Value.class, DataQuery.of("price"));
	public static final Key<MapValue<String, String>> HOME_LIST = KeyFactory.makeMapKey(String.class, String.class, DataQuery.of("homes"));
}
