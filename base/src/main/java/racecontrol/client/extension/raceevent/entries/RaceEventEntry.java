/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.raceevent.entries;

import racecontrol.client.data.SessionId;
import racecontrol.gui.lpui.table.LPTable;

/**
 *
 * @author Leonard
 */
public abstract class RaceEventEntry {

    private final SessionId sessionId;
    private final int sessionTime;
    private final String typeDescriptor;
    private final boolean hasReplay;
    private int replayTime;

    public RaceEventEntry(
            SessionId sessionId,
            int sessionTime,
            String typeDescriptor,
            boolean hasReplay) {
        this.sessionId = sessionId;
        this.sessionTime = sessionTime;
        this.typeDescriptor = typeDescriptor;
        this.hasReplay = hasReplay;
        this.replayTime = 0;
    }

    /**
     * Sets the replay time for this event.
     *
     * @param replayTime the replay time.
     */
    public void setReplayTime(int replayTime) {
        this.replayTime = replayTime;
    }

    /**
     * Returns the render to render the info column for this event.
     *
     * @return The rendere to use.
     */
    public abstract LPTable.CellRenderer getInfoRenderer();
    
    /**
     * Returns info about this event as a string.
     * @return the info.
     */
    public abstract String getInfo();
    
    public SessionId getSessionId(){
        return sessionId;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public String getTypeDescriptor() {
        return typeDescriptor;
    }

    public boolean isHasReplay() {
        return hasReplay;
    }

    public int getReplayTime() {
        return replayTime;
    }

}
