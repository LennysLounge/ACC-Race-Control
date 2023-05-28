/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol;

import racecontrol.client.protocol.enums.SessionPhase;
import racecontrol.client.protocol.enums.SessionType;

/**
 *
 * @author Leonard
 */
public class SessionInfo {

    int eventIndex;
    int sessionIndex;
    SessionType sessionType = SessionType.NONE;
    SessionPhase phase = SessionPhase.NONE;
    int sessionTime;
    int sessionEndTime;
    int focusedCarIndex;
    String activeCameraSet = "";
    String activeCamera = "";
    String currentHudPage = "";
    boolean replayPlaying;
    int replaySessionTime;
    int replayRemainingTime;
    int timeOfDay;
    byte ambientTemp;
    byte trackTemp;
    byte cloudLevel;
    byte rainLevel;
    byte wetness;
    LapInfo bestSessionLap = new LapInfo();

    public SessionInfo() {
    }

    public SessionInfo(int eventIndex, int sessionIndex, SessionType sessionType, SessionPhase phase, int sessionTime,
            int sessionEndTime, int focusedCarIndex, String activeCameraSet, String activeCamera,
            String currentHudPage, boolean replayPlaying, int replaySessionTime, int replayRemainingTime,
            int timeOfDay, byte ambientTemp, byte trackTemp, byte cloudLevel, byte rainLevel, byte wetness,
            LapInfo bestSessionLap) {
        this.eventIndex = eventIndex;
        this.sessionIndex = sessionIndex;
        this.sessionType = sessionType;
        this.phase = phase;
        this.sessionTime = sessionTime;
        this.sessionEndTime = sessionEndTime;
        this.focusedCarIndex = focusedCarIndex;
        this.activeCameraSet = activeCameraSet;
        this.activeCamera = activeCamera;
        this.currentHudPage = currentHudPage;
        this.replayPlaying = replayPlaying;
        this.replaySessionTime = replaySessionTime;
        this.replayRemainingTime = replayRemainingTime;
        this.timeOfDay = timeOfDay;
        this.ambientTemp = ambientTemp;
        this.trackTemp = trackTemp;
        this.cloudLevel = cloudLevel;
        this.rainLevel = rainLevel;
        this.wetness = wetness;
        this.bestSessionLap = bestSessionLap;
    }

    public int getEventIndex() {
        return eventIndex;
    }

    public int getSessionIndex() {
        return sessionIndex;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public SessionPhase getPhase() {
        return phase;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public int getSessionEndTime() {
        return sessionEndTime;
    }

    public int getFocusedCarIndex() {
        return focusedCarIndex;
    }

    public String getActiveCameraSet() {
        return activeCameraSet;
    }

    public String getActiveCamera() {
        return activeCamera;
    }

    public String getCurrentHudPage() {
        return currentHudPage;
    }

    public boolean isReplayPlaying() {
        return replayPlaying;
    }

    public int getReplaySessionTime() {
        return replaySessionTime;
    }

    public int getReplayRemainingTime() {
        return replayRemainingTime;
    }

    public int getTimeOfDay() {
        return timeOfDay;
    }

    public byte getAmbientTemp() {
        return ambientTemp;
    }

    public byte getTrackTemp() {
        return trackTemp;
    }

    public byte getCloudLevel() {
        return cloudLevel;
    }

    public byte getRainLevel() {
        return rainLevel;
    }

    public byte getWetness() {
        return wetness;
    }

    public LapInfo getBestSessionLap() {
        return bestSessionLap;
    }

    @Override
    public String toString() {
        return "SessionInfo{"
                + "eventIndex=" + eventIndex
                + ", sessionIndex=" + sessionIndex
                + ", sessionType=" + sessionType
                + ", phase=" + phase
                + ", sessionTime=" + sessionTime
                + ", sessionEndTime=" + sessionEndTime
                + ", focusedCarIndex=" + focusedCarIndex
                + ", activeCameraSet=" + activeCameraSet
                + ", activeCamera=" + activeCamera
                + ", currentHudPage=" + currentHudPage
                + ", replayPlaying=" + replayPlaying
                + ", replaySessionTime=" + replaySessionTime
                + ", replayRemainingTime=" + replayRemainingTime
                + ", timeOfDay=" + timeOfDay
                + ", ambientTemp=" + ambientTemp
                + ", trackTemp=" + trackTemp
                + ", cloudLevel=" + cloudLevel
                + ", rainLevel=" + rainLevel
                + ", wetness=" + wetness
                + ", bestSessionLap=" + bestSessionLap
                + '}';
    }

}
