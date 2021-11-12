/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.extension.dangerdetection.DangerDetectionExtension;
import static racecontrol.client.extension.statistics.CarProperties.IS_WHITE_FLAG;
import static racecontrol.client.extension.statistics.CarProperties.IS_YELLOW_FLAG;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class FlagProcessor
        extends StatisticsProcessor {

    /**
     * Reference to the yellow flag detection.
     */
    private final DangerDetectionExtension dangerExtension;

    public FlagProcessor(Map<Integer, WritableCarStatistics> cars) {
        super(cars);
        dangerExtension = DangerDetectionExtension.getInstance();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            int carId = ((RealtimeCarUpdateEvent) e).getInfo().getCarId();
            var stats = getCars().get(carId);
            stats.put(IS_WHITE_FLAG, dangerExtension.isCarWhiteFlag(carId));
            stats.put(IS_YELLOW_FLAG, dangerExtension.isCarYellowFlag(carId));
        }
    }

}
