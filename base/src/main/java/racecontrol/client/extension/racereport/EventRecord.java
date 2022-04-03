/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.racereport;

import racecontrol.client.protocol.SessionId;

/**
 *
 * @author Leonard
 */
public class EventRecord {
    private final int sessionTime;
    private final String typeDesciptor;
    private final String info;
    private final int replayTime;
    private final SessionId sessionId;

    public EventRecord(int sessionTime, String typeDesciptor, String info, int replayTime, SessionId sessionId) {
        this.sessionTime = sessionTime;
        this.typeDesciptor = typeDesciptor;
        this.info = info;
        this.replayTime = replayTime;
        this.sessionId = sessionId;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public String getTypeDesciptor() {
        return typeDesciptor;
    }

    public String getInfo() {
        return info;
    }

    public int getReplayTime() {
        return replayTime;
    }
    
    public SessionId getSessionId(){
        return sessionId;
    }
    
    
}
