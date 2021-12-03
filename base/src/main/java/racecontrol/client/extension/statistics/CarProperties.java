/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import java.util.ArrayList;
import java.util.List;
import racecontrol.client.data.DriverInfo;
import racecontrol.client.data.SessionId;
import racecontrol.client.data.enums.CarLocation;
import racecontrol.client.data.enums.CarModel;
import racecontrol.client.data.enums.DriverCategory;
import racecontrol.client.extension.statistics.WritableCarStatistics.Key;

/**
 *
 * @author Leonard
 */
public interface CarProperties {

    public final Key<Integer> CAR_ID = new Key<>(Integer.class, 0);

    // Identity
    public final Key<String> FIRSTNAME = new Key<>(String.class, "");
    public final Key<String> SURNAME = new Key<>(String.class, "");
    public final Key<String> FULL_NAME = new Key<>(String.class, "");
    public final Key<String> NAME = new Key<>(String.class, "");
    public final Key<String> SHORT_NAME = new Key<>(String.class, "");
    public final Key<Integer> CAR_NUMBER = new Key<>(Integer.class, 0);
    public final Key<CarModel> CAR_MODEL = new Key<>(CarModel.class, CarModel.ERROR);
    public final Key<DriverCategory> CATEGORY = new Key<>(DriverCategory.class, DriverCategory.BRONZE);
    public final Key<Integer> DRIVER_INDEX = new Key<>(Integer.class, 0);
    public final Key<DriverList> DRIVER_LIST = new Key<>(DriverList.class, new DriverList());
    public final Key<String> TEAM_NAME = new Key<>(String.class, "");
    // Laps
    public final Key<Integer> CURRENT_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> LAST_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> BEST_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> DELTA = new Key<>(Integer.class, 0);
    public final Key<Boolean> CURRENT_LAP_INVALID = new Key<>(Boolean.class, false);
    public final Key<Boolean> LAST_LAP_INVALID = new Key<>(Boolean.class, false);
    public final Key<Boolean> BEST_LAP_INVALID = new Key<>(Boolean.class, false);
    public final Key<Integer> LAP_COUNT = new Key<>(Integer.class, 0);
    // Gaps
    public final Key<Integer> LAP_TIME_GAP_TO_SESSION_BEST = new Key<>(Integer.class, 0);
    public final Key<Integer> GAP_TO_LEADER = new Key<>(Integer.class, 0);
    public final Key<Integer> GAP_TO_POSITION_AHEAD = new Key<>(Integer.class, 0);
    public final Key<Integer> GAP_TO_CAR_AHEAD = new Key<>(Integer.class, 0);
    public final Key<Integer> LAPS_BEHIND_LEADER = new Key<>(Integer.class, 0);
    public final Key<Boolean> LAPS_BEHIND_SPLIT = new Key<>(Boolean.class, false);
    public final Key<Float> RACE_DISTANCE_BEHIND_LEADER = new Key<>(Float.class, 0f);
    // Sectors
    public final Key<Integer> CURRENT_SECTOR_ONE_CALC = new Key<>(Integer.class, 0);
    public final Key<Integer> CURRENT_SECTOR_TWO_CALC = new Key<>(Integer.class, 0);
    public final Key<Integer> CURRENT_SECTOR_THREE_CALC = new Key<>(Integer.class, 0);
    public final Key<Integer> CURRENT_SECTOR_ONE = new Key<>(Integer.class, 0);
    public final Key<Integer> CURRENT_SECTOR_TWO = new Key<>(Integer.class, 0);
    public final Key<Integer> CURRENT_SECTOR_THREE = new Key<>(Integer.class, 0);
    public final Key<Integer> LAST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public final Key<Integer> LAST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public final Key<Integer> LAST_SECTOR_THREE = new Key<>(Integer.class, 0);
    public final Key<Integer> BEST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public final Key<Integer> BEST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public final Key<Integer> BEST_SECTOR_THREE = new Key<>(Integer.class, 0);
    // Status
    public final Key<Integer> POSITION = new Key<>(Integer.class, 0);
    public final Key<Integer> CUP_POSITION = new Key<>(Integer.class, 0);
    public final Key<Boolean> IS_IN_PITS = new Key<>(Boolean.class, false);
    public final Key<CarLocation> CAR_LOCATION = new Key<>(CarLocation.class, CarLocation.NONE);
    public final Key<Boolean> IS_FOCUSED_ON = new Key<>(Boolean.class, false);
    public final Key<Boolean> IS_SESSION_BEST = new Key<>(Boolean.class, false);
    public final Key<Boolean> IS_WHITE_FLAG = new Key<>(Boolean.class, false);
    public final Key<Boolean> IS_YELLOW_FLAG = new Key<>(Boolean.class, false);
    // Realtime position
    public final Key<Float> RACE_DISTANCE_SIMPLE = new Key<>(Float.class, 0f);
    public final Key<Float> RACE_DISTANCE_COMPLEX = new Key<>(Float.class, 0f);
    public final Key<Integer> REALTIME_POSITION = new Key<>(Integer.class, 0);
    public final Key<Boolean> USE_REALTIME_POS = new Key<>(Boolean.class, false);
    // Session over
    public final Key<Boolean> SESSION_FINISHED = new Key<>(Boolean.class, false);
    // Overtake indicator
    public final Key<Integer> OVERTAKE_INDICATOR = new Key<>(Integer.class, 0);
    // Places lost and gained
    public final Key<Integer> RACE_START_POSITION = new Key<>(Integer.class, 0);
    public final Key<Boolean> RACE_START_POSITION_ACCURATE = new Key<>(Boolean.class, false);
    public final Key<Integer> PLACES_GAINED = new Key<>(Integer.class, 0);
    // Pitlane time
    public final Key<Integer> PITLANE_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> PITLANE_TIME_STATIONARY = new Key<>(Integer.class, 0);
    public final Key<Integer> PITLANE_COUNT = new Key<>(Integer.class, 0);
    public final Key<Boolean> PITLANE_COUNT_ACCURATE = new Key<>(Boolean.class, false);
    // Speed
    public final Key<Integer> SPEED_TRAP_SPEED = new Key<>(Integer.class, 0);
    public final Key<Integer> MAX_SPEED_TRAP_SPEED = new Key<>(Integer.class, 0);
    public final Key<Integer> MAXIMUM_SPEED = new Key<>(Integer.class, 0);
    public final Key<Integer> MAX_MAXIMUM_SPEED = new Key<>(Integer.class, 0);
    public final Key<Integer> CURRENT_SPEED = new Key<>(Integer.class, 0);
    // Stint time
    public final Key<Integer> DRIVER_STINT_TIME = new Key<>(Integer.class, 0);
    public final Key<Boolean> DRIVER_STINT_TIME_ACCURATE = new Key<>(Boolean.class, false);

    // Session
    public final Key<Integer> SESSION_BEST_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> SESSION_BEST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public final Key<Integer> SESSION_BEST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public final Key<Integer> SESSION_BEST_SECTOR_THREE = new Key<>(Integer.class, 0);
    public final Key<SessionId> SESSION_ID = new Key<>(SessionId.class, new SessionId());

    public class DriverList {

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
