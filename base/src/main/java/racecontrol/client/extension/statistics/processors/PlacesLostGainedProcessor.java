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
import static racecontrol.client.data.enums.SessionPhase.PRESESSION;
import static racecontrol.client.data.enums.SessionPhase.SESSION;
import static racecontrol.client.data.enums.SessionType.RACE;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import static racecontrol.client.extension.statistics.CarStatistics.PLACES_GAINED;
import static racecontrol.client.extension.statistics.CarStatistics.POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.RACE_START_POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.eventbus.Event;
import static racecontrol.client.extension.statistics.CarStatistics.RACE_START_POSITION_ACCURATE;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_FINISHED;

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

    public PlacesLostGainedProcessor(Map<Integer, CarStatisticsWritable> cars) {
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
            client.getModel().cars.values().stream()
                    .map(car -> car.raw)
                    .map(carInfo -> getCars().get(carInfo.getCarId()))
                    .forEach(carStats -> {

                        if (carStats.get(RACE_START_POSITION) == 0) {
                            carStats.put(RACE_START_POSITION, carStats.get(POSITION));
                            carStats.put(RACE_START_POSITION_ACCURATE, false);
                        }

                        if (!carStats.get(SESSION_FINISHED)) {
                            int lostGained = carStats.get(REALTIME_POSITION) - carStats.get(RACE_START_POSITION);
                            carStats.put(PLACES_GAINED, lostGained);
                        }
                    });
        }
    }

    private void sessionChanged(SessionInfo info) {
        // set the starting position for each car to 0
        client.getModel().cars.values().stream()
                .map(car -> car.raw)
                .map(carInfo -> getCars().get(carInfo.getCarId()))
                .forEach(carStats -> {
                    carStats.put(PLACES_GAINED, 0);
                    carStats.put(RACE_START_POSITION, 0);
                    carStats.put(RACE_START_POSITION_ACCURATE, false);
                });
    }

    private void sessionPhaseChanged(SessionInfo info) {
        if (info.getSessionType() == RACE
                && (info.getPhase() == SESSION
                || info.getPhase() == PRESESSION)) {
            client.getModel().cars.values().stream()
                    .map(car -> car.raw)
                    .map(carInfo -> getCars().get(carInfo.getCarId()))
                    .forEach(carStats -> {
                        carStats.put(RACE_START_POSITION, carStats.get(REALTIME_POSITION));
                        carStats.put(RACE_START_POSITION_ACCURATE, true);
                    });
        }
    }
}
