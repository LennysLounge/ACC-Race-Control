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
    // Sectors
    public static final Key<Integer> CURRENT_SECTOR_ONE_CALC = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_TWO_CALC = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_THREE_CALC = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_ONE = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_TWO = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_THREE = new Key<>(Integer.class, 0);
    // Status
    public static final Key<Boolean> IS_FOCUSED_ON = new Key<>(Boolean.class, false);
    public static final Key<Boolean> IS_SESSION_BEST = new Key<>(Boolean.class, false);
    public static final Key<Boolean> IS_WHITE_FLAG = new Key<>(Boolean.class, false);
    public static final Key<Boolean> IS_YELLOW_FLAG = new Key<>(Boolean.class, false);
    // Realtime position
    public static final Key<Float> RACE_DISTANCE_SIMPLE = new Key<>(Float.class, 0f);
    public static final Key<Float> RACE_DISTANCE_COMPLEX = new Key<>(Float.class, 0f);
    public static final Key<Float> SPLINE_POS = new Key<>(Float.class, 0f);
    public static final Key<Integer> REALTIME_POSITION = new Key<>(Integer.class, 0);
    public static final Key<Boolean> USE_REALTIME_POS = new Key<>(Boolean.class, false);
    // Session over
    public static final Key<Boolean> SESSION_FINISHED = new Key<>(Boolean.class, false);
    // Overtake indicator
    public static final Key<Integer> OVERTAKE_INDICATOR = new Key<>(Integer.class, 0);
    // Places lost and gained
    public static final Key<Integer> RACE_START_POSITION = new Key<>(Integer.class, 0);
    public static final Key<Boolean> RACE_START_POSITION_ACCURATE = new Key<>(Boolean.class, false);
    public static final Key<Integer> PLACES_GAINED = new Key<>(Integer.class, 0);
    // Pitlane time
    public static final Key<Integer> PITLANE_TIME = new Key<>(Integer.class, 0);
    public static final Key<Integer> PITLANE_TIME_STATIONARY = new Key<>(Integer.class, 0);
    public static final Key<Integer> PITLANE_COUNT = new Key<>(Integer.class, 0);
    public static final Key<Boolean> PITLANE_COUNT_ACCURATE = new Key<>(Boolean.class, false);
    // Speed
    public static final Key<Integer> SPEED_TRAP_SPEED = new Key<>(Integer.class, 0);
    public static final Key<Integer> MAX_SPEED_TRAP_SPEED = new Key<>(Integer.class, 0);
    public static final Key<Integer> MAXIMUM_SPEED = new Key<>(Integer.class, 0);
    public static final Key<Integer> MAX_MAXIMUM_SPEED = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SPEED = new Key<>(Integer.class, 0);
    // Stint time
    public static final Key<Integer> DRIVER_STINT_TIME = new Key<>(Integer.class, 0);
    public static final Key<Boolean> DRIVER_STINT_TIME_ACCURATE = new Key<>(Boolean.class, false);

    // Session
    public static final Key<Integer> SESSION_BEST_LAP_TIME = new Key<>(Integer.class, 0);
    public static final Key<Integer> SESSION_BEST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public static final Key<Integer> SESSION_BEST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public static final Key<Integer> SESSION_BEST_SECTOR_THREE = new Key<>(Integer.class, 0);
    public static final Key<SessionId> SESSION_ID = new Key<>(SessionId.class, new SessionId());

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
