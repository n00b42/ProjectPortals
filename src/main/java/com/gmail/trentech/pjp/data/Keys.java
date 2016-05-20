package com.gmail.trentech.pjp.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.pjp.data.object.Home;
import com.gmail.trentech.pjp.data.object.Sign;

public class Keys {

	public static final Key<Value<Sign>> SIGN = KeyFactory.makeSingleKey(Sign.class, Value.class, DataQuery.of("sign"));
	public static final Key<MapValue<String, Home>> HOMES = KeyFactory.makeMapKey(String.class, Home.class, DataQuery.of("homes"));
}
