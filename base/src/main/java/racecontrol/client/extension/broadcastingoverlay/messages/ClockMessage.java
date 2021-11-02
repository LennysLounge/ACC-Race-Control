/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.messages;

/**
 *
 * @author Leonard
 */
public class ClockMessage {

    private final String type = "ClockMessage";

    private int timeRemaining;
    private String sessionName;
    private int localTime;

    public ClockMessage() {
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
    
    public String getType(){
        return type;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getLocalTime() {
        return localTime;
    }

    public void setLocalTime(int localTime) {
        this.localTime = localTime;
    }
}
