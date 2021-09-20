/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import racecontrol.client.data.enums.DriverCategory;
import racecontrol.client.extension.statistics.WriteableCarStatistics.Key;

/**
 *
 * @author Leonard
 */
public interface CarProperties {

    public final Key<Integer> CAR_ID = new Key(Integer.class, 0);

    public final Key<Integer> POSITION = new Key(Integer.class, 0);
    public final Key<Integer> CUP_POSITION = new Key(Integer.class, 0);
    // Identity
    public final Key<String> FIRSTNAME = new Key(String.class, "");
    public final Key<String> SURNAME = new Key(String.class, "");
    public final Key<String> FULL_NAME = new Key(String.class, "");
    public final Key<String> NAME = new Key(String.class, "");
    public final Key<String> SHORT_NAME = new Key(String.class, "");
    public final Key<Integer> CAR_NUMBER = new Key(Integer.class, 0);
    public final Key<Byte> CAR_MODEL = new Key(Byte.class, 0);
    public final Key<DriverCategory> CATEGORY = new Key(DriverCategory.class, DriverCategory.BRONZE);
    // Laps
    public final Key<Integer> CURRENT_LAP_TIME = new Key(Integer.class, 0);
    public final Key<Integer> LAST_LAP_TIME = new Key(Integer.class, 0);
    public final Key<Integer> BEST_LAP_TIME = new Key(Integer.class, 0);
    public final Key<Integer> DELTA = new Key(Integer.class, 0);
    public final Key<Integer> GAP_TO_SESSION_BEST_LAP = new Key(Integer.class, 0);
    public final Key<Boolean> IS_LAP_INVALID = new Key(Boolean.class, false);
    public final Key<Integer> LAP_COUNT = new Key(Integer.class, 0);

    public final Key<Boolean> IS_IN_PITS = new Key(Boolean.class, false);

}
