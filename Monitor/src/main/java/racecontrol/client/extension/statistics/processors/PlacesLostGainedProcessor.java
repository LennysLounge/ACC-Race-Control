/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.SessionPhase.SESSION;
import static racecontrol.client.data.enums.SessionType.RACE;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.PLACES_GAINED;
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import static racecontrol.client.extension.statistics.CarProperties.RACE_START_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import racecontrol.eventbus.Event;
import static racecontrol.client.extension.statistics.CarProperties.RACE_START_POSITION_ACCURATE;

/**
 *
 * @author Leonard
 */
public class PlacesLostGainedProcessor
        extends StatisticsProcessor {

    private static final Logger LOG = Logger.getLogger(PlacesLostGainedProcessor.class.getName());

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;

    public PlacesLostGainedProcessor(Map<Integer, WritableCarStatistics> cars) {
        super(cars);
        this.client = AccBroadcastingClient.getClient();
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
            client.getModel().getCarsInfo().values().stream()
                    .map(carInfo -> getCars().get(carInfo.getCarId()))
                    .forEach(carStats -> {

                        if (carStats.get(RACE_START_POSITION) == 0) {
                            carStats.put(RACE_START_POSITION, carStats.get(POSITION));
                            carStats.put(RACE_START_POSITION_ACCURATE, false);
                        }

                        int lostGained = carStats.get(REALTIME_POSITION) - carStats.get(RACE_START_POSITION);
                        carStats.put(PLACES_GAINED, lostGained);
                    });
        }
    }

    private void sessionChanged(SessionInfo info) {
        // set the starting position for each car to 0
        client.getModel().getCarsInfo().values().stream()
                .map(carInfo -> getCars().get(carInfo.getCarId()))
                .forEach(carStats -> {
                    carStats.put(PLACES_GAINED, 0);
                    carStats.put(RACE_START_POSITION, 0);
                    carStats.put(RACE_START_POSITION_ACCURATE, false);
                });
    }

    private void sessionPhaseChanged(SessionInfo info) {
        if (info.getSessionType() == RACE
                && info.getPhase() == SESSION) {
            client.getModel().getCarsInfo().values().stream()
                    .map(carInfo -> getCars().get(carInfo.getCarId()))
                    .forEach(carStats -> {
                        carStats.put(RACE_START_POSITION, carStats.get(REALTIME_POSITION));
                        carStats.put(RACE_START_POSITION_ACCURATE, true);
                    });
        }
    }
}
