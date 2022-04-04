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
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
import static racecontrol.client.protocol.enums.SessionPhase.PRESESSION;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_ID;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarStatistics.RACE_DISTANCE_COMPLEX;
import static racecontrol.client.extension.statistics.CarStatistics.RACE_DISTANCE_SIMPLE;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_FINISHED;
import static racecontrol.client.extension.statistics.CarStatistics.SPLINE_POS;
import static racecontrol.client.extension.statistics.CarStatistics.USE_REALTIME_POS;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.model.Car;
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

    public RealtimePositionProcessor(Map<Integer, CarStatisticsWritable> cars) {
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
        CarStatisticsWritable carStats = getCars().get(info.getCarId());

        carStats.put(SPLINE_POS, info.getSplinePosition());

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
        var carsSorted = client.getModel().cars.values().stream()
                .map(car -> getCars().get(car.id))
                .sorted((c1, c2) -> c2.get(RACE_DISTANCE_COMPLEX).compareTo(c1.get(RACE_DISTANCE_COMPLEX)))
                .collect(Collectors.toList());

        int pos = 1;
        for (var carStats : carsSorted) {
            if (shouldUseRealtimePosition(info, carStats)) {
                carStats.put(REALTIME_POSITION, pos);
            } else {
                Car car = client.getModel().cars.get(carStats.get(CAR_ID));
                int position = 0;
                if (car != null) {
                    position = car.position;
                }
                carStats.put(REALTIME_POSITION, position);
            }
            carStats.put(USE_REALTIME_POS, shouldUseRealtimePosition(info, carStats));
            pos++;
        }
    }

    private boolean shouldUseRealtimePosition(SessionInfo info,
            CarStatisticsWritable carStats) {
        return info.getSessionType() == RACE
                && info.getSessionTime() > 15000
                && !carStats.get(SESSION_FINISHED);
    }

    private void resetDistances() {
        client.getModel().cars.values().forEach(car -> {
            CarStatisticsWritable carS = getCars().get(car.id);
            carS.put(RACE_DISTANCE_SIMPLE, 0f);
            carS.put(RACE_DISTANCE_COMPLEX, 0f);
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
