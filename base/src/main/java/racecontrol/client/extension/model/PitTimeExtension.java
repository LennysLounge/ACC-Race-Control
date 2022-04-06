/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import java.util.HashMap;
import java.util.Map;
import racecontrol.client.ClientExtension;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
import static racecontrol.client.protocol.enums.SessionPhase.PRESESSION;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class PitTimeExtension
        extends ClientExtension
        implements EventListener {

    /**
     * Map from carId's to the time they entered the pitlane.
     */
    private final Map<Integer, Long> pitEntryTimestamp = new HashMap<>();
    /**
     * Maps from carId's to the time they stopped moving.
     */
    private final Map<Integer, Long> pitStationaryTimestamp = new HashMap<>();

    public PitTimeExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            carUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged(((SessionPhaseChangedEvent) e).getSessionInfo());
        }
    }

    private void sessionPhaseChanged(SessionInfo info) {
        if (info.getPhase() == PRESESSION) {
            pitEntryTimestamp.clear();
            pitStationaryTimestamp.clear();
            getWritableModel().cars.values().stream()
                    .forEach(car -> {
                        car.pitLaneTime = 0;
                        car.pitLaneTimeStationary = 0;
                        car.pitlaneCount = 0;
                        car.pitlaneCountAccurate = true;
                    });
        }
    }

    private void carUpdate(RealtimeInfo info) {
        if (info.getLocation() == PITLANE) {

            long now = System.currentTimeMillis();
            Car car = getWritableModel().cars.get(info.getCarId());

            // if car has entered the pits, set the timestamp, reset the
            // pit timers and increment pit count.
            if (!pitEntryTimestamp.containsKey(car.id)) {
                pitEntryTimestamp.put(car.id, now);
                car.pitLaneTime = 0;
                car.pitLaneTimeStationary = 0;
                car.pitlaneCount++;
            }

            // update pit time.
            car.pitLaneTime = (int) (now - pitEntryTimestamp.get(car.id));

            // update time stationary
            if (pitStationaryTimestamp.containsKey(car.id)) {
                int diff = (int) (now - pitStationaryTimestamp.get(car.id));
                car.pitLaneTimeStationary = car.pitLaneTimeStationary + diff;
                pitStationaryTimestamp.remove(info.getCarId());
            }
            if (info.getKMH() == 0) {
                pitStationaryTimestamp.put(info.getCarId(), now);
            }
        } else {
            // if not it pits, remove timestamps.
            pitEntryTimestamp.remove(info.getCarId());
            pitStationaryTimestamp.remove(info.getCarId());
        }
    }
}
