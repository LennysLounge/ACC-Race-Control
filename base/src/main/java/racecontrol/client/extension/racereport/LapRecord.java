/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.racereport;

/**
 *
 * @author Leonard
 */
public class LapRecord {
    
    /**
     * Lap time of this lap.
     */
    private final int lapTime;
    /**
     * Delta to the leader.
     */
    private final int deltaToLeader;

    public LapRecord(int lapTime, int deltaToLeader) {
        this.lapTime = lapTime;
        this.deltaToLeader = deltaToLeader;
    }

    public float getLapTime() {
        return lapTime;
    }

    public int getDeltaToLeader() {
        return deltaToLeader;
    }
}
