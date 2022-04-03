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
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import static racecontrol.client.extension.statistics.CarStatistics.OVERTAKE_INDICATOR;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;

/**
 * Finds when a car has finished its session.
 *
 * @author Leonard
 */
public class OvertakeProcessor
        extends StatisticsProcessor {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(OvertakeProcessor.class.getName());
    /**
     * Reference to the game.
     */
    private final AccBroadcastingClient client;
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

    public OvertakeProcessor(Map<Integer, CarStatisticsWritable> cars) {
        super(cars);
        this.client = AccBroadcastingClient.getClient();
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

        for (Car car : client.getModel().cars.values()) {
            if (car.realtimeRaw.isDefault()) {
                return;
            }

            CarStatisticsWritable stats = getCars().get(car.id);

            if (prevPositions.containsKey(car.id)) {
                int diff = stats.get(REALTIME_POSITION) - prevPositions.get(car.id);
                if (diff != 0) {
                    stats.put(OVERTAKE_INDICATOR, diff);
                    timestamps.put(car.id, System.currentTimeMillis());
                }
            }
            prevPositions.put(car.id, stats.get(REALTIME_POSITION));
        }
    }

    private void resetIndicators() {
        long now = System.currentTimeMillis();
        var iter = timestamps.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if ((now - entry.getValue()) > INDICATOR_TIME) {
                CarStatisticsWritable stats = getCars().get(entry.getKey());
                stats.put(OVERTAKE_INDICATOR, 0);
                iter.remove();
            }
        }
    }

}
