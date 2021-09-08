/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.entries;

import racecontrol.lpgui.gui.LPTable;
import racecontrol.lpgui.gui.LPTableModel;

/**
 *
 * @author Leonard
 */
public abstract class RaceEventEntry {
    
    private final float sessionTime;
    private final String typeDescriptor;
    private final boolean hasReplay;
    private float replayTime;
    
    public RaceEventEntry(float sessionTime,
            String typeDescriptor,
            boolean hasReplay,
            float replayTime){
        this.sessionTime = sessionTime;
        this.typeDescriptor = typeDescriptor;
        this.hasReplay = hasReplay;
        this.replayTime = replayTime;
    }
    /**
     * Sets the replay time for this event.
     * @param replayTime the replay time.
     */
    public void setReplayTime(float replayTime){
        this.replayTime = replayTime;
    }
    /**
     * Returns the render to render the info column for this event.
     * @return The rendere to use.
     */
    public abstract LPTable.CellRenderer getInfoRenderer();

    public float getSessionTime() {
        return sessionTime;
    }

    public String getTypeDescriptor() {
        return typeDescriptor;
    }

    public boolean isHasReplay() {
        return hasReplay;
    }

    public float getReplayTime() {
        return replayTime;
    }
    
    
}
