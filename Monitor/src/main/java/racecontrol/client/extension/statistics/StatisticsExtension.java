/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.client.data.CarInfo;
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.statistics.processors.DataProcessor;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * Gathers data and statistics for the cars.
 *
 * @author Leonard
 */
public class StatisticsExtension
        implements EventListener, AccBroadcastingExtension {

    /**
     * Singelton instance.
     */
    private static StatisticsExtension instance;
    /**
     * Maps carId's to car statistics.
     */
    private final Map<Integer, WriteableCarStatistics> cars = new HashMap<>();
    /**
     * List of processors.
     */
    private final List<StatisticsProcessor> processors = new ArrayList<>();

    /**
     * Gives the instance of the statistics extension.
     *
     * @return StatisticsExtension
     */
    public static StatisticsExtension getInstance() {
        if (instance == null) {
            instance = new StatisticsExtension();
        }
        return instance;
    }

    private StatisticsExtension() {
        EventBus.register(this);
        processors.add(new DataProcessor(cars));
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof CarConnectedEvent) {
            CarInfo car = ((CarConnectedEvent) e).getCar();
            cars.put(car.getCarId(), new WriteableCarStatistics());
        } else if (e instanceof RealtimeUpdateEvent) {
            processors.forEach((processor)
                    -> processor.onRealtimeUpdate(((RealtimeUpdateEvent) e).getSessionInfo()));
        } else if (e instanceof RealtimeCarUpdateEvent) {
            processors.forEach((processor)
                    -> processor.onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo()));
        }
    }

    public CarStatistics getCar(int carId) {
        return new CarStatistics(cars.get(carId).getProperties());
    }

}
