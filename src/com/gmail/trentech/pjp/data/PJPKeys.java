package com.gmail.trentech.pjp.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.pjp.data.Portal;

public class PJPKeys {

	public static final Key<Value<Portal>> PORTAL = KeyFactory.makeSingleKey(Portal.class, Value.class, DataQuery.of("PORTAL"));
}
