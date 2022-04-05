/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.eventbus.Event;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_ID;
import static racecontrol.client.extension.statistics.CarStatistics.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarStatistics.IS_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_ID;
import racecontrol.client.model.Car;

/**
 * A Basic processor for any easily available data.
 *
 * @author Leonard
 */
public class DataProcessor extends StatisticsProcessor {

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;

    public DataProcessor(Map<Integer, CarStatisticsWritable> cars) {
        super(cars);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            onRealtimeUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    public void onRealtimeCarUpdate(RealtimeInfo info) {
        Car car = client.getModel().cars.get(info.getCarId());
        if (!getCars().containsKey(info.getCarId())) {
            return;
        }
        CarStatisticsWritable carStats = getCars().get(info.getCarId());
        carStats.put(CAR_ID, info.getCarId());
    }

    public void onRealtimeUpdate(SessionInfo info) {
        for (CarStatisticsWritable stats : getCars().values()) {
            Car car = client.getModel().cars.get(stats.get(CAR_ID));
            stats.put(IS_FOCUSED_ON, info.getFocusedCarIndex() == stats.get(CAR_ID));
            stats.put(SESSION_ID, client.getModel().currentSessionId);
            stats.put(IS_SESSION_BEST,
                    info.getBestSessionLap().getLapTimeMS() != Integer.MAX_VALUE
                    && info.getBestSessionLap().getLapTimeMS() == car.bestLap.getLapTimeMS()
            );
        }
    }

}
