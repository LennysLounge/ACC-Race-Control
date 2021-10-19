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
public class WritableCarStatistics {

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
        if (!properties.containsKey(key)) {
            return key.defaultValue;
        }
        return key.type.cast(properties.get(key));
    }

    /**
     * Returns the properties for this car.
     *
     * @return Map of keys to objects.
     */
    public Map<Key<?>, Object> getProperties() {
        return properties;
    }

    /**
     * Key to use to identify car statistics properties.
     *
     * @param <T> Type of the object this key referes to.
     */
    public static class Key<T> {

        /**
         * Type of the object this key referes to.
         */
        final Class<T> type;
        /**
         * Default value for this key if the property does not exist.
         */
        final T defaultValue;

        public Key(Class<T> type, T defaultValue) {
            this.type = type;
            this.defaultValue = defaultValue;
        }
    }

}
