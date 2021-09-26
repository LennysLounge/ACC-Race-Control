/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import racecontrol.client.data.enums.CarLocation;
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
    public final Key<Byte> CAR_MODEL = new Key<>(Byte.class, (byte) 0);
    public final Key<DriverCategory> CATEGORY = new Key<>(DriverCategory.class, DriverCategory.BRONZE);
    // Laps
    public final Key<Integer> CURRENT_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> LAST_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> BEST_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> DELTA = new Key<>(Integer.class, 0);
    public final Key<Boolean> IS_LAP_INVALID = new Key<>(Boolean.class, false);
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

    // Session
    public final Key<Integer> SESSION_BEST_LAP_TIME = new Key<>(Integer.class, 0);
    public final Key<Integer> SESSION_BEST_SECTOR_ONE = new Key<>(Integer.class, 0);
    public final Key<Integer> SESSION_BEST_SECTOR_TWO = new Key<>(Integer.class, 0);
    public final Key<Integer> SESSION_BEST_SECTOR_THREE = new Key<>(Integer.class, 0);

}
