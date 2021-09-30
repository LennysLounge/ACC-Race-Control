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
import racecontrol.client.extension.statistics.processors.DataProcessor;
import racecontrol.client.extension.statistics.processors.GapProcessor;
import racecontrol.client.extension.statistics.processors.OvertakeProcessor;
import racecontrol.client.extension.statistics.processors.PlacesLostGainedProcessor;
import racecontrol.client.extension.statistics.processors.RealtimePositionProcessor;
import racecontrol.client.extension.statistics.processors.SectorTimesProcessor;
import racecontrol.client.extension.statistics.processors.SessionOverProcessor;
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
    private final Map<Integer, WritableCarStatistics> cars = new HashMap<>();
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
        processors.add(new DataProcessor(cars));
        processors.add(new SectorTimesProcessor(cars));
        processors.add(new SessionOverProcessor(cars));
        processors.add(new RealtimePositionProcessor(cars));
        processors.add(new GapProcessor(cars));
        processors.add(new OvertakeProcessor(cars));
        processors.add(new PlacesLostGainedProcessor(cars));
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof CarConnectedEvent) {
            CarInfo car = ((CarConnectedEvent) e).getCar();
            cars.put(car.getCarId(), new WritableCarStatistics());
        }

        processors.forEach(processor -> processor.onEvent(e));
    }

    public CarStatistics getCar(int carId) {
        return new CarStatistics(cars.get(carId).getProperties());
    }

}
