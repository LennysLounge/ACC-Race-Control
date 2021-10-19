/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.CarLocation.PITLANE;
import static racecontrol.client.data.enums.SessionPhase.PRESESSION;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT_ACCURATE;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME_STATIONARY;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class PitTimeProcessor
        extends StatisticsProcessor {

    private static final Logger LOG = Logger.getLogger(PitTimeProcessor.class.getName());

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;
    /**
     * Map from carId's to the time they entered the pitlane.
     */
    private final Map<Integer, Long> pitEntryTimestamp = new HashMap<>();
    /**
     * Maps from carId's to the time they stopped moving.
     */
    private final Map<Integer, Long> pitStationaryTimestamp = new HashMap<>();

    public PitTimeProcessor(Map<Integer, WritableCarStatistics> cars) {
        super(cars);
        this.client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            realtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof SessionChangedEvent) {
        } else if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged(((SessionPhaseChangedEvent) e).getSessionInfo());
        }
    }

    private void sessionPhaseChanged(SessionInfo info) {
        if (info.getPhase() == PRESESSION) {
            pitEntryTimestamp.clear();
            pitStationaryTimestamp.clear();
            client.getModel().getCarsInfo().values().stream()
                    .map(carInfo -> getCars().get(carInfo.getCarId()))
                    .forEach(carStats -> {
                        carStats.put(PITLANE_TIME, 0);
                        carStats.put(PITLANE_TIME_STATIONARY, 0);
                        carStats.put(PITLANE_COUNT, 0);
                        carStats.put(PITLANE_COUNT_ACCURATE, true);
                    });
        }
    }

    private void realtimeCarUpdate(RealtimeInfo info) {
        if (info.getLocation() == PITLANE) {
            
            long now = System.currentTimeMillis();
            WritableCarStatistics carStats = getCars().get(info.getCarId());

            // if car has entered the pits, set the timestamp, reset the
            // pit timers and increment pit count.
            if (!pitEntryTimestamp.containsKey(info.getCarId())) {
                pitEntryTimestamp.put(info.getCarId(), now);
                carStats.put(PITLANE_TIME, 0);
                carStats.put(PITLANE_TIME_STATIONARY, 0);
                carStats.put(PITLANE_COUNT, carStats.get(PITLANE_COUNT) + 1);
            }

            // update pit time.
            carStats.put(PITLANE_TIME, (int) (now - pitEntryTimestamp.get(info.getCarId())));

            // update time stationary
            if (pitStationaryTimestamp.containsKey(info.getCarId())) {
                int diff = (int) (now - pitStationaryTimestamp.get(info.getCarId()));
                carStats.put(PITLANE_TIME_STATIONARY, carStats.get(PITLANE_TIME_STATIONARY) + diff);
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
