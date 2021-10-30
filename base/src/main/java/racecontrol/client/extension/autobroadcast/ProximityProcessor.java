/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import java.util.HashMap;
import java.util.Map;
import racecontrol.client.extension.statistics.CarProperties;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_AHEAD;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class ProximityProcessor
        implements RatingProcessor {

    /**
     * Reference to the statistics extension.
     */
    private final StatisticsExtension statistics;
    /**
     * Distance where a car is battling
     */
    private final int BATTLE_DISTANCE = 1500;

    private final Map<Integer, Float> prevProximity = new HashMap<>();

    public ProximityProcessor() {
        statistics = StatisticsExtension.getInstance();
    }

    @Override
    public Entry calculateRating(Entry entry) {
        int carId = entry.getCarInfo().getCarId();
        CarStatistics stats = statistics.getCar(carId);

        if (stats.get(CarProperties.IS_FOCUSED_ON)
                && stats.get(CarProperties.OVERTAKE_INDICATOR) < 0) {
            entry = entry.withOvertake(1f);
        }

        /*
        if (prevProximity.containsKey(carId)) {
            float delta = prevProximity.get(carId)
                    - stats.get(GAP_TO_POSITION_AHEAD);
            entry = entry.withProximityDelta(delta / 1000f);
        }
         */
        float value = 0;
        if (!stats.get(CarProperties.IS_IN_PITS)
                && stats.get(CarProperties.REALTIME_POSITION) != 1) {

            float gap = stats.get(GAP_TO_POSITION_AHEAD) * 1f;
            float battleRating = clampZeroToOne(1 - gap / (BATTLE_DISTANCE));

            value = battleRating * 0.2f
                    + prevProximity.getOrDefault(carId, battleRating) * 0.8f;
        }
        prevProximity.put(carId, value);
        return entry.withProximity(value);
    }

    @Override
    public void onEvent(Event e) {
    }

    private float clampZeroToOne(float v) {
        return Math.max(0, Math.min(1, v));
    }

}
