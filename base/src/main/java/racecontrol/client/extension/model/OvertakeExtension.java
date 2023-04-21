/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import java.util.HashMap;
import java.util.Map;
import racecontrol.client.ClientExtension;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;

/**
 * Finds when a car has finished its session.
 *
 * @author Leonard
 */
public class OvertakeExtension
        extends ClientExtension {

    /**
     * Ms the indicator should be visible for.
     */
    private final int INDICATOR_TIME = 15000;
    /**
     * Holds the previous position a car was in. Maps CarId to position.
     */
    private final Map<Integer, Integer> prevPositions = new HashMap<>();
    /**
     * Timestamp for when a indicator started to show. Maps carId to timestamp.
     */
    private final Map<Integer, Long> timestamps = new HashMap<>();

    public OvertakeExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof AfterPacketReceivedEvent) {
            resetIndicators();
        } else if (e instanceof ConnectionClosedEvent) {
            prevPositions.clear();
            timestamps.clear();
        }
    }

    private void sessionUpdate(SessionInfo info) {
        if (info.getSessionType() != RACE) {
            return;
        }

        for (Car car : getWritableModel().getCars()) {
            if (prevPositions.containsKey(car.id)) {
                int diff = car.realtimePosition - prevPositions.get(car.id);
                if (diff != 0) {
                    car.overtakeIndicator = diff;
                    timestamps.put(car.id, System.currentTimeMillis());
                }
            }
            prevPositions.put(car.id, car.realtimePosition);
        }
    }

    private void resetIndicators() {
        long now = System.currentTimeMillis();
        var iter = timestamps.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if ((now - entry.getValue()) > INDICATOR_TIME) {
                getWritableModel().getCar(entry.getKey()).ifPresent(car -> {
                    car.overtakeIndicator = 0;
                });
                iter.remove();
            }
        }
    }

}
