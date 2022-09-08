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

/**
 * General extension for basic model properties.
 *
 * @author Leonard
 */
public class ModelExtension
        extends ClientExtension {

    public ModelExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
            findBestSectors(((RealtimeUpdateEvent) e).getSessionInfo());
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

    private void findBestSectors(SessionInfo info) {
        // find best sectors.
        int bestSectorOne = Integer.MAX_VALUE;
        int bestSectorTwo = Integer.MAX_VALUE;
        int bestSectorThree = Integer.MAX_VALUE;
        if (info.getBestSessionLap().getLapTimeMS() != Integer.MAX_VALUE) {
            for (Car car : getWritableModel().cars.values()) {
                if (car.bestLap.getSplits().get(0) < bestSectorOne) {
                    bestSectorOne = car.bestLap.getSplits().get(0);
                }
                if (car.bestLap.getSplits().get(1) < bestSectorTwo) {
                    bestSectorTwo = car.bestLap.getSplits().get(1);
                }
                if (car.bestLap.getSplits().get(2) < bestSectorThree) {
                    bestSectorThree = car.bestLap.getSplits().get(2);
                }
            }
        }
        getWritableModel().session.sessionBestSplits.set(0, bestSectorOne);
        getWritableModel().session.sessionBestSplits.set(1, bestSectorTwo);
        getWritableModel().session.sessionBestSplits.set(2, bestSectorThree);
    }

}
