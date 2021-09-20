package racecontrol.client.extension.statistics;

/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
import java.util.Map;
import racecontrol.client.extension.statistics.WriteableCarStatistics.Key;

/**
 * Car statistics where the properties are read only.
 *
 * @author Leonard
 */
public class CarStatistics {

    /**
     * Map holds the properties.
     */
    private final Map<Key<?>, Object> properties;

    public CarStatistics(Map<Key<?>, Object> properties) {
        this.properties = properties;
    }

    /**
     * Returns the property for the key.
     *
     * @param <T> Type of the value.
     * @param key The key to look for.
     * @return The value of type T.
     */
    public <T> T get(Key<T> key) {
        return key.type.cast(properties.get(key));
    }
}
