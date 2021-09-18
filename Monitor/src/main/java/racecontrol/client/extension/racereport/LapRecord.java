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
    private final float lapTime;
    /**
     * Delta to the leader.
     */
    private final float deltaToLeader;

    public LapRecord(float lapTime, float deltaToLeader) {
        this.lapTime = lapTime;
        this.deltaToLeader = deltaToLeader;
    }

    public float getLapTime() {
        return lapTime;
    }

    public float getDeltaToLeader() {
        return deltaToLeader;
    }
}
