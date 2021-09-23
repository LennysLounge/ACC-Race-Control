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

    private final Map<Integer, WritableCarStatistics> cars;

    public StatisticsProcessor(Map<Integer, WritableCarStatistics> cars) {
        this.cars = cars;
    }

    public Map<Integer, WritableCarStatistics> getCars() {
        return cars;
    }

    public abstract void onEvent(Event e);
}
