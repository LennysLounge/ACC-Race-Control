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
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * Finds when a car has finished its session.
 *
 * @author Leonard
 */
public class OvertakeExtension
        extends ClientExtension
        implements EventListener {

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
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof AfterPacketReceivedEvent) {
            resetIndicators();
        }
    }

    private void sessionUpdate(SessionInfo info) {
        if (info.getSessionType() != RACE) {
            return;
        }

        for (Car car : getWritableModel().cars.values()) {
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
                getWritableModel().cars.get(entry.getKey()).overtakeIndicator = 0;
                iter.remove();
            }
        }
    }

}
