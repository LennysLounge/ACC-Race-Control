package racecontrol.client.extension.statistics;

/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import racecontrol.client.model.Driver;
import racecontrol.client.protocol.SessionId;

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

    /**
     * Creates a statistics object with properties.
     *
     * @param properties the properties for this object.
     */
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
        if (!properties.containsKey(key)) {
            return key.defaultValue;
        }
        return key.type.cast(properties.get(key));
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

    //
    //             Properties
    //
    public static final Key<Integer> CAR_ID = new Key<>(Integer.class, 0);

    public static class DriverList {

        private final List<Driver> drivers;

        public DriverList() {
            this(new ArrayList<>());
        }

        public DriverList(List<Driver> drivers) {
            this.drivers = drivers;
        }

        public List<Driver> getDrivers() {
            return drivers;
        }
    }
}
