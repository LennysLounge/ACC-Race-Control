package racecontrol.client.extension.statistics;

/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import racecontrol.client.data.DriverInfo;
import racecontrol.client.data.SessionId;
import racecontrol.client.data.enums.CarLocation;
import racecontrol.client.data.enums.CarModel;
import racecontrol.client.data.enums.DriverCategory;

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

    // Identity
    public static final Key<String> FIRSTNAME = new Key<>(String.class, "");
    public static final Key<String> SURNAME = new Key<>(String.class, "");
    public static final Key<String> FULL_NAME = new Key<>(String.class, "");
    public static final Key<String> NAME = new Key<>(String.class, "");
    public static final Key<String> SHORT_NAME = new Key<>(String.class, "");
    public static final Key<Integer> CAR_NUMBER = new Key<>(Integer.class, 0);
    public static final Key<CarModel> CAR_MODEL = new Key<>(CarModel.class, CarModel.ERROR);
    public static final Key<DriverCategory> CATEGORY = new Key<>(DriverCategory.class, DriverCategory.BRONZE);
    public static final Key<Integer> DRIVER_INDEX = new Key<>(Integer.class, 0);
    public static final Key<DriverList> DRIVER_LIST = new Key<>(DriverList.class, new DriverList());
    public static final Key<String> TEAM_NAME = new Key<>(String.class, "");
    // Laps
    public static final Key<Integer> CURRENT_LAP_TIME = new Key<>(Integer.class, 0);
    public static final Key<Integer> LAST_LAP_TIME = new Key<>(Integer.class, 0);
    public static final Key<Integer> BEST_LAP_TIME = new Key<>(Integer.class, 0);
    public static final Key<Integer> DELTA = new Key<>(Integer.class, 0);
    public static final Key<Integer> PREDICTED_LAP_TIME = new Key<>(Integer.class, 0);
    public static final Key<Boolean> CURRENT_LAP_INVALID = new Key<>(Boolean.class, false);
    public static final Key<Boolean> LAST_LAP_INVALID = new Key<>(Boolean.class, false);
    public static final Key<Boolean> BEST_LAP_INVALID = new Key<>(Boolean.class, false);
    public static final Key<Integer> LAP_COUNT = new Key<>(Integer.class, 0);
    // Gaps
    public static final Key<Integer> LAP_TIME_GAP_TO_SESSION_BEST = new Key<>(Integer.class, 0);
    public static final Key<Integer> GAP_TO_LEADER = new Key<>(Integer.class, 0);
    public static final Key<Integer> GAP_TO_POSITION_AHEAD = new Key<>(Integer.class, 0);
    public static final Key<Integer> GAP_TO_POSITION_BEHIND = new Key<>(Integer.class, 0);
    public static final Key<Integer> GAP_TO_CAR_AHEAD = new Key<>(Integer.class, 0);
    public static final Key<Integer> GAP_TO_CAR_BEHIND = new Key<>(Integer.class, 0);
    public static final Key<Integer> LAPS_BEHIND_LEADER = new Key<>(Integer.class, 0);
    public static final Key<Boolean> LAPS_BEHIND_SPLIT = new Key<>(Boolean.class, false);
    public static final Key<Float> RACE_DISTANCE_BEHIND_LEADER = new Key<>(Float.class, 0f);
    // Sectors
    public static final Key<Integer> CURRENT_SECTOR_ONE_CALC = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_TWO_CALC = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_THREE_CALC = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_ONE = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_TWO = new Key<>(Integer.class, 0);
    public static final Key<Integer> CURRENT_SECTOR_THREE = new Key<>(Integer.class, 0);
    public static final Key<Integer> LAST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public static final Key<Integer> LAST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public static final Key<Integer> LAST_SECTOR_THREE = new Key<>(Integer.class, 0);
    public static final Key<Integer> BEST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public static final Key<Integer> BEST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public static final Key<Integer> BEST_SECTOR_THREE = new Key<>(Integer.class, 0);
    // Status
    public static final Key<Integer> POSITION = new Key<>(Integer.class, 0);
    public static final Key<Integer> CUP_POSITION = new Key<>(Integer.class, 0);
    public static final Key<Boolean> IS_IN_PITS = new Key<>(Boolean.class, false);
    public static final Key<CarLocation> CAR_LOCATION = new Key<>(CarLocation.class, CarLocation.NONE);
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

        private final List<DriverInfo> drivers;

        public DriverList() {
            this(new ArrayList<>());
        }

        public DriverList(List<DriverInfo> drivers) {
            this.drivers = drivers;
        }

        public List<DriverInfo> getDrivers() {
            return drivers;
        }
    }
}
