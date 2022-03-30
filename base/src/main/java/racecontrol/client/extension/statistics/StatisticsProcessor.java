/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import java.util.Map;
import racecontrol.eventbus.Event;

/**
 * Describes methods a statistics processor must implement.
 *
 * @author Leonard
 */
public abstract class StatisticsProcessor {

    private final Map<Integer, CarStatisticsWritable> cars;

    public StatisticsProcessor(Map<Integer, CarStatisticsWritable> cars) {
        this.cars = cars;
    }

    public Map<Integer, CarStatisticsWritable> getCars() {
        return cars;
    }

    public abstract void onEvent(Event e);
}
