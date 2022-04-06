/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import racecontrol.client.ClientExtension;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.SessionPhase.PRESESSION;
import static racecontrol.client.protocol.enums.SessionPhase.SESSION;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class PlacesLostGainedExtension
        extends ClientExtension
        implements EventListener {

    public PlacesLostGainedExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof SessionChangedEvent) {
            sessionChanged(((SessionChangedEvent) e).getSessionInfo());
        } else if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged(((SessionPhaseChangedEvent) e).getSessionInfo());
        }
    }

    private void sessionUpdate(SessionInfo info) {
        // update the places lost gained.
        if (info.getSessionType() == RACE
                && info.getPhase().getId() >= SESSION.getId()) {
            getWritableModel().cars.values().stream()
                    .forEach(car -> {
                        if (car.raceStartPosition == 0) {
                            car.raceStartPosition = car.position;
                            car.raceStartPositionAccurate = false;
                        }
                    });
        }
    }

    private void sessionChanged(SessionInfo info) {
        // set the starting position for each car to 0
        getWritableModel().cars.values().stream()
                .forEach(car -> {
                    car.raceStartPosition = 0;
                    car.raceStartPositionAccurate = false;
                });
    }

    private void sessionPhaseChanged(SessionInfo info) {
        if (info.getSessionType() == RACE
                && (info.getPhase() == SESSION
                || info.getPhase() == PRESESSION)) {
            getWritableModel().cars.values().stream()
                    .forEach(car -> {
                        car.raceStartPosition = car.realtimePosition;
                        car.raceStartPositionAccurate = true;
                    });
        }
    }
}
