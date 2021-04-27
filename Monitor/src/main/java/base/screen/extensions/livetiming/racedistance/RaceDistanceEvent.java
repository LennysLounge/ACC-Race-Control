/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming.racedistance;

import base.screen.eventbus.Event;
import base.screen.networking.data.RealtimeInfo;

/**
 *
 * @author Leonard
 */
public class RaceDistanceEvent
        extends Event {

    private final RealtimeInfo realtimeInfo;
    private final float raceDistance;

    public RaceDistanceEvent(RealtimeInfo realtimeInfo, float raceDistance) {
        this.realtimeInfo = realtimeInfo;
        this.raceDistance = raceDistance;
    }

    public RealtimeInfo getRealtimeInfo() {
        return realtimeInfo;
    }

    public float getRaceDistance() {
        return raceDistance;
    }

}
