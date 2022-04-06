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
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.ClientExtension;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_ID;
import racecontrol.client.model.Car;

/**
 * Gathers data and statistics for the cars.
 *
 * @author Leonard
 */
public class StatisticsExtension extends ClientExtension
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static StatisticsExtension instance;
    /**
     * Maps carId's to car statistics.
     */
    private final Map<Integer, CarStatisticsWritable> cars = new HashMap<>();
    /**
     * List of processors.
     */
    private final List<StatisticsProcessor> processors;

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
        this.processors = new ArrayList<>();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof CarConnectedEvent) {
            Car car = ((CarConnectedEvent) e).getCar();
            var stats = new CarStatisticsWritable();
            stats.put(CAR_ID, car.id);
            cars.put(car.id, stats);
        }

        processors.forEach(processor -> processor.onEvent(e));
    }

    public CarStatistics getCar(int carId) {
        return cars.getOrDefault(carId, new CarStatisticsWritable()).getReadonly();
    }

}
