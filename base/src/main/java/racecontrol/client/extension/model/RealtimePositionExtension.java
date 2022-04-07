/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import java.util.stream.Collectors;
import racecontrol.client.ClientExtension;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.client.model.Car;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.SessionPhase.PRESESSION;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * Calculates the realtime position by using a corrected race distance.
 *
 * @author Leonard
 */
public class RealtimePositionExtension
        extends ClientExtension
        implements EventListener {

    public RealtimePositionExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            calculateRaceDistance(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            findRealtimePosition(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof SessionChangedEvent) {
            resetDistances();
        } else if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged(((SessionPhaseChangedEvent) e).getSessionInfo());
        }
    }

    private void calculateRaceDistance(RealtimeInfo info) {
        Car car = getWritableModel().cars.get(info.getCarId());
        float raceDistance = info.getSplinePosition() + info.getLaps();
        // correct for the error around the lap reset.
        if (info.getSplinePosition() > 0.95f
                || info.getSplinePosition() < 0.05f) {
            float diffToLastUpdate
                    = raceDistance - car.raceDistance;
            if (diffToLastUpdate < -0.5) {
                raceDistance += 1;
            }
            if (diffToLastUpdate > 0.5) {
                raceDistance -= 1;
            }
        }
        car.raceDistance = raceDistance;
    }

    private void findRealtimePosition(SessionInfo info) {
        // sort cars based on their race distance.
        var carsSorted = getWritableModel().cars.values().stream()
                .sorted((c1, c2) -> -Float.compare(c1.raceDistance, c2.raceDistance))
                .collect(Collectors.toList());

        int pos = 1;
        for (var car : carsSorted) {
            if (info.getSessionType() == RACE
                    && info.getSessionTime() > 15000
                    && !car.isCheckeredFlag) {
                car.realtimePosition = pos;
            } else {
                car.realtimePosition = car.position;
            }
            pos++;
        }
    }

    private void resetDistances() {
        getWritableModel().cars.values().forEach(car -> {
            car.raceDistance = 0;
        });
    }

    private void sessionPhaseChanged(SessionInfo info) {
        // if a car is in the pits during the presession phase it teleported
        // to the pits by not pressing drive. This puts them ahead of everyone.
        // give a penalty to avoid this.
        if (info.getPhase() == PRESESSION) {
            getWritableModel().cars.values().forEach(car -> {
                if (car.isInPit()) {
                    car.raceDistance = -1f;
                }
            });
        }
    }

}
