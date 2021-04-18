/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.data;

import base.screen.networking.enums.SessionPhase;
import base.screen.networking.enums.SessionType;

/**
 *
 * @author Leonard
 */
public class SessionInfo {

    int eventIndex;
    int sessionIndex;
    SessionType sessionType = SessionType.NONE;
    SessionPhase phase = SessionPhase.NONE;
    float sessionTime;
    float sessionEndTime;
    int focusedCarIndex;
    String activeCameraSet = "";
    String activeCamera = "";
    String currentHudPage = "";
    boolean isReplayPlaying;
    float replaySessionTime;
    float replayRemainingTime;
    float timeOfDay;
    byte ambientTemp;
    byte trackTemp;
    byte cloudLevel;
    byte rainLevel;
    byte wetness;
    LapInfo bestSessionLap = new LapInfo();

    public SessionInfo() {
    }

    public SessionInfo(int eventIndex, int sessionIndex, SessionType sessionType, SessionPhase phase, float sessionTime,
            float sessionEndTime, int focusedCarIndex, String activeCameraSet, String activeCamera,
            String currentHudPage, boolean isReplayPlaying, float replaySessionTime, float replayRemainingTime,
            float timeOfDay, byte ambientTemp, byte trackTemp, byte cloudLevel, byte rainLevel, byte wetness,
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
        this.isReplayPlaying = isReplayPlaying;
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

    public float getSessionTime() {
        return sessionTime;
    }

    public float getSessionEndTime() {
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

    public boolean getIsReplayPlaying() {
        return isReplayPlaying;
    }

    public float getReplaySessionTime() {
        return replaySessionTime;
    }

    public float getReplayRemainingTime() {
        return replayRemainingTime;
    }

    public float getTimeOfDay() {
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
}
