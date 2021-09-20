/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Car statistics class that holds a map of properties.
 *
 * @author Leonard
 */
public class WriteableCarStatistics {

    /**
     * Map holds the properties.
     */
    private final Map<Key<?>, Object> properties = new HashMap<>();

    /**
     * Add a new property
     *
     * @param <T> Class of the value.
     * @param key The key place the value under.
     * @param value The value to save.
     */
    public <T> void put(Key<T> key, T value) {
        properties.put(key, value);
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

    public Map<Key<?>, Object> getProperties() {
        return properties;
    }

    public static class Key<T> {

        final Class<T> type;

        public Key(Class<T> type) {
            this.type = type;
        }
    }

 
}
