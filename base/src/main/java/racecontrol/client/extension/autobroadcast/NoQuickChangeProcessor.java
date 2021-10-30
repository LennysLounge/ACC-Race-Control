/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class NoQuickChangeProcessor
        implements RatingProcessor {

    private int currentFocusId = 0;
    private long changeTimeStamp = 0;

    private final float TIME = 60000f;

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    public void sessionUpdate(SessionInfo info) {
        if (info.getFocusedCarIndex() != currentFocusId) {
            currentFocusId = info.getFocusedCarIndex();
            changeTimeStamp = System.currentTimeMillis();
        }
    }

    @Override
    public Entry calculateRating(Entry entry) {
        if (entry.getCarInfo().getCarId() == currentFocusId) {
            long now = System.currentTimeMillis();
            float value = 1 - (now - changeTimeStamp) / TIME;
            value = Math.max(0, Math.min(1, value));
            value = 0.2f + value * 0.8f;
            return entry.withChange(value);
        }
        return entry.withChange(0f);
    }

}
