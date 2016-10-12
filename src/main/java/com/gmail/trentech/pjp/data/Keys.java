package com.gmail.trentech.pjp.data;

import java.util.Map;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.pjp.data.portal.Home;
import com.gmail.trentech.pjp.data.portal.Sign;
import com.google.common.reflect.TypeToken;

public class Keys {

	private static final TypeToken<Map<String, Home>> MAP_HOMES = new TypeToken<Map<String,Home>>() {
		private static final long serialVersionUID = 2375752428409666167L;
    };
	private static final TypeToken<MapValue<String, Home>> VALUE_HOMES = new TypeToken<MapValue<String,Home>>() {
		private static final long serialVersionUID = -6254940587461711335L;
    };    
	private static final TypeToken<Value<Sign>> VALUE_SIGN = new TypeToken<Value<Sign>>() {
		private static final long serialVersionUID = 395242399877312340L;
    };    
	private static final TypeToken<Sign> SIGN_TOKEN = new TypeToken<Sign>() {
		private static final long serialVersionUID = -8726734755833911770L;
    };
    
	public static final Key<Value<Sign>> SIGN = KeyFactory.makeSingleKey(SIGN_TOKEN, VALUE_SIGN, DataQuery.of("sign"), "pjp:sign", "sign");
	public static final Key<MapValue<String, Home>> HOMES = KeyFactory.makeMapKey(MAP_HOMES, VALUE_HOMES, DataQuery.of("homes"), "pjp:homes", "homes");
}
