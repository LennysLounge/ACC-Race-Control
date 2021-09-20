/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import racecontrol.client.extension.statistics.WriteableCarStatistics.Key;

/**
 *
 * @author Leonard
 */
public interface CarProperties {

    public final Key<Integer> POSITION = new Key(Integer.class);
    public final Key<Integer> CUP_POSITION = new Key(Integer.class);
    // Identity
    public final Key<String> FIRSTNAME = new Key(String.class);
    public final Key<String> SURNAME = new Key(String.class);
    public final Key<String> FULL_NAME = new Key(String.class);
    public final Key<String> NAME = new Key(String.class);
    public final Key<String> SHORT_NAME = new Key(String.class);
    public final Key<Integer> CAR_NUMBER = new Key(Integer.class);
    // Laps
    public final Key<Integer> CURRENT_LAP_TIME = new Key(Integer.class);
    public final Key<Integer> LAST_LAP_TIME = new Key(Integer.class);
    public final Key<Integer> BEST_LAP_TIME = new Key(Integer.class);
    public final Key<Integer> DELTA = new Key(Integer.class);
    public final Key<Integer> GAP_TO_SESSION_BEST_LAP = new Key(Integer.class);
    public final Key<Boolean> IS_LAP_INVALID = new Key(Boolean.class);
    public final Key<Integer> LAP_COUNT = new Key(Integer.class);
    
    public final Key<Boolean> IS_IN_PITS = new Key(Boolean.class);

}
