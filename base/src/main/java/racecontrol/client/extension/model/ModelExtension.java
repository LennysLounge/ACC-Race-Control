/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import racecontrol.client.ClientExtension;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.model.Car;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * General extension for basic model properties.
 *
 * @author Leonard
 */
public class ModelExtension
        extends ClientExtension
        implements EventListener {

    public ModelExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    public void onSessionUpdate(SessionInfo info) {
        for (Car car : getWritableModel().cars.values()) {
            car.isFocused = info.getFocusedCarIndex() == car.id;
            if (info.getBestSessionLap().getLapTimeMS() != Integer.MAX_VALUE) {
                car.isSessionBestLaptime = info.getBestSessionLap().getLapTimeMS() == car.bestLap.getLapTimeMS();
            }
        }
    }

}
