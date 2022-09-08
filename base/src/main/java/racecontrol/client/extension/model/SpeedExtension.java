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
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class SpeedExtension
        extends ClientExtension {

    /**
     * Track data for the current track.
     */
    private TrackData trackData;
    /**
     * Maps carId's to their spline positions.
     */
    private final Map<Integer, Float> prevPosition = new HashMap<>();

    public SpeedExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            carUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate();
        } else if (e instanceof TrackDataEvent) {
            trackData = ((TrackDataEvent) e).getTrackData();
            resetData();
        } else if (e instanceof SessionChangedEvent) {
            resetData();
        }
    }

    private void carUpdate(RealtimeInfo info) {
        Car car = getWritableModel().cars.get(info.getCarId());

        // Save maximum speed.
        if (car.KMH > car.maxKMH) {
            car.maxKMH = car.KMH;
        }

        // Save speed trap speed.
        if (trackData != null) {
            float prevPos = prevPosition.getOrDefault(car.id, car.splinePosition);

            if (prevPos < trackData.getSpeedTrapLine()
                    && car.splinePosition > trackData.getSpeedTrapLine()) {
                if (car.KMH > car.speedTrapKMH) {
                    car.speedTrapKMH = car.KMH;
                }
            }
            prevPosition.put(car.id, car.splinePosition);
        }
    }

    private void sessionUpdate() {
        // find fastest speed trap
        int maxSpeedTrap = 0;
        int maxSpeed = 0;
        for (Car car : getWritableModel().cars.values()) {
            maxSpeedTrap = Math.max(maxSpeedTrap, car.speedTrapKMH);
            maxSpeed = Math.max(maxSpeed, car.maxKMH);
        }
        getWritableModel().session.maxKMH = maxSpeed;
        getWritableModel().session.maxSpeedTrapKMH = maxSpeedTrap;
    }

    private void resetData() {
        getWritableModel().cars.values().forEach(car -> {
            car.maxKMH = 0;
            car.speedTrapKMH = 0;
        });
    }

}
