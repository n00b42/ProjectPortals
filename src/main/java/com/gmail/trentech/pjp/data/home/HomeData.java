package com.gmail.trentech.pjp.data.home;

import static com.gmail.trentech.pjp.data.Keys.HOME_LIST;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.MapValue;

import com.gmail.trentech.pjp.portals.Home;
import com.google.common.base.Preconditions;

public class HomeData extends AbstractMappedData<String, Home, HomeData, ImmutableHomeData> {

	public HomeData(Map<String, Home> value) {		
		super(value, HOME_LIST);
	}

	public MapValue<String, Home> homes() {
        return Sponge.getRegistry().getValueFactory().createMapValue(HOME_LIST, getValue());
    }
	
	@Override
	public Optional<Home> get(String key) {
		if(getValue().containsKey(key)){
			return Optional.of(getValue().get(key));
		}
		return Optional.empty();
	}

	@Override
	public Set<String> getMapKeys() {
		return getValue().keySet();
	}

	@Override
	public HomeData put(String key, Home value) {
		getValue().put(key, value);
		return this;
	}

	@Override
	public HomeData putAll(Map<? extends String, ? extends Home> map) {
		getValue().putAll(map);
		return this;
	}

	@Override
	public HomeData remove(String key) {
		getValue().remove(key);
		return this;
	}

    @Override
    public Optional<HomeData> fill(DataHolder dataHolder, MergeFunction mergeFn) {
        HomeData homeData = Preconditions.checkNotNull(mergeFn).merge(copy(), dataHolder.get(HomeData.class).orElse(copy()));
        return Optional.of(set(HOME_LIST, homeData.get(HOME_LIST).get()));
    }

    @SuppressWarnings("unchecked")
	@Override
    public Optional<HomeData> from(DataContainer container) {
        if (container.contains(HOME_LIST.getQuery())) {
            return Optional.of(new HomeData((Map<String, Home>) container.getMap(HOME_LIST.getQuery()).get()));
        }
        return Optional.empty();
    }

	@Override
	public HomeData copy() {
		return new HomeData(getValue());
	}

    @Override
    public int compareTo(HomeData data) {
        return this.compareTo(data);
    }
    
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public ImmutableHomeData asImmutable() {
		return new ImmutableHomeData(getValue());
	}
}