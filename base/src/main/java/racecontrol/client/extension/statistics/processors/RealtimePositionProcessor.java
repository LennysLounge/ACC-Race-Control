/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.CarLocation.PITLANE;
import static racecontrol.client.data.enums.SessionPhase.PRESESSION;
import static racecontrol.client.data.enums.SessionType.RACE;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_COMPLEX;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_SIMPLE;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_FINISHED;
import static racecontrol.client.extension.statistics.CarProperties.USE_REALTIME_POS;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class RealtimePositionProcessor
        extends StatisticsProcessor {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(RealtimePositionProcessor.class.getName());
    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;

    public RealtimePositionProcessor(Map<Integer, WritableCarStatistics> cars) {
        super(cars);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof SessionChangedEvent) {
            resetDistances();
        } else if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged(((SessionPhaseChangedEvent) e).getSessionInfo());
        }
    }

    private void onRealtimeCarUpdate(RealtimeInfo info) {
        WritableCarStatistics carStats = getCars().get(info.getCarId());

        float raceDistance = info.getSplinePosition() + info.getLaps();
        carStats.put(RACE_DISTANCE_SIMPLE, raceDistance);

        if (info.getSplinePosition() > 0.95f
                || info.getSplinePosition() < 0.05f) {
            float diffToLastUpdate
                    = raceDistance - carStats.get(RACE_DISTANCE_COMPLEX);
            if (diffToLastUpdate < -0.5) {
                raceDistance += 1;
            }
            if (diffToLastUpdate > 0.5) {
                raceDistance -= 1;
            }
        }
        carStats.put(RACE_DISTANCE_COMPLEX, raceDistance);
    }

    private void onSessionUpdate(SessionInfo info) {
        // sort cars based on their complex race distance.
        var carsSorted = client.getModel().getCarsInfo().values().stream()
                .map(carInfo -> getCars().get(carInfo.getCarId()))
                .sorted((c1, c2) -> c2.get(RACE_DISTANCE_COMPLEX).compareTo(c1.get(RACE_DISTANCE_COMPLEX)))
                .collect(Collectors.toList());

        int pos = 1;
        for (var carStats : carsSorted) {
            if (shouldUseRealtimePosition(info, carStats)) {
                carStats.put(REALTIME_POSITION, pos);
            } else {
                carStats.put(REALTIME_POSITION,
                        client.getModel().getCar(carStats.get(CAR_ID))
                                .getRealtime().getPosition());
            }
            carStats.put(USE_REALTIME_POS, shouldUseRealtimePosition(info, carStats));
            pos++;
        }
    }

    private boolean shouldUseRealtimePosition(SessionInfo info,
            WritableCarStatistics carStats) {
        return info.getSessionType() == RACE
                && info.getSessionTime() > 15000
                && !carStats.get(SESSION_FINISHED);
    }

    private void resetDistances() {
        client.getModel().getCarsInfo().values().forEach(carInfo -> {
            WritableCarStatistics car = getCars().get(carInfo.getCarId());
            car.put(RACE_DISTANCE_SIMPLE, 0f);
            car.put(RACE_DISTANCE_COMPLEX, 0f);
        });
    }

    private void sessionPhaseChanged(SessionInfo info) {
        if (info.getPhase() == PRESESSION) {
            getCars().values().forEach(carStats -> {
                if (carStats.get(CAR_LOCATION) == PITLANE) {
                    carStats.put(RACE_DISTANCE_COMPLEX, -1f);
                }
            });
        }
    }

}
