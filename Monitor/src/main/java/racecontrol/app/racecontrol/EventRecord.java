/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

/**
 *
 * @author Leonard
 */
public class EventRecord {
    private final float sessionTime;
    private final String typeDesciptor;
    private final String info;
    private final float replayTime;

    public EventRecord(float sessionTime, String typeDesciptor, String info, float replayTime) {
        this.sessionTime = sessionTime;
        this.typeDesciptor = typeDesciptor;
        this.info = info;
        this.replayTime = replayTime;
    }

    public float getSessionTime() {
        return sessionTime;
    }

    public String getTypeDesciptor() {
        return typeDesciptor;
    }

    public String getInfo() {
        return info;
    }

    public float getReplayTime() {
        return replayTime;
    }
    
    
}
