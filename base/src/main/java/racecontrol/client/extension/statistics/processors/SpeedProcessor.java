/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.HashMap;
import java.util.Map;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SPEED;
import static racecontrol.client.extension.statistics.CarStatistics.MAXIMUM_SPEED;
import static racecontrol.client.extension.statistics.CarStatistics.MAX_MAXIMUM_SPEED;
import static racecontrol.client.extension.statistics.CarStatistics.MAX_SPEED_TRAP_SPEED;
import static racecontrol.client.extension.statistics.CarStatistics.SPEED_TRAP_SPEED;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class SpeedProcessor
        extends StatisticsProcessor {

    /**
     * Track data for the current track.
     */
    private TrackData trackData;
    /**
     * Maps carId's to their spline positions.
     */
    private final Map<Integer, Float> prevPosition = new HashMap<>();

    public SpeedProcessor(Map<Integer, CarStatisticsWritable> cars) {
        super(cars);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            realtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate();
        } else if (e instanceof TrackDataEvent) {
            trackData = ((TrackDataEvent) e).getTrackData();
            resetData();
        } else if (e instanceof SessionChangedEvent) {
            resetData();
        }
    }

    private void realtimeCarUpdate(RealtimeInfo info) {
        CarStatisticsWritable carStats = getCars().get(info.getCarId());
        carStats.put(CURRENT_SPEED, info.getKMH());

        // Save maximum speed.
        if (info.getKMH() > carStats.get(MAXIMUM_SPEED)) {
            carStats.put(MAXIMUM_SPEED, info.getKMH());
        }

        // Save speed trap speed.
        if (trackData != null) {
            float prevPos = prevPosition.getOrDefault(info.getCarId(),
                    info.getSplinePosition());

            if (prevPos < trackData.getSpeedTrapLine()
                    && info.getSplinePosition() > trackData.getSpeedTrapLine()) {
                if (info.getKMH() > carStats.get(SPEED_TRAP_SPEED)) {
                    carStats.put(SPEED_TRAP_SPEED, info.getKMH());
                }
            }
            prevPosition.put(info.getCarId(), info.getSplinePosition());
        }
    }

    private void sessionUpdate() {
        // find fastest speed trap
        int maxSpeedTrap = 0;
        int maxSpeed = 0;
        for (var carStat : getCars().values()) {
            maxSpeedTrap = Math.max(maxSpeedTrap, carStat.get(SPEED_TRAP_SPEED));
            maxSpeed = Math.max(maxSpeed, carStat.get(MAXIMUM_SPEED));
        }
        for (var carStat : getCars().values()) {
            carStat.put(MAX_SPEED_TRAP_SPEED, maxSpeedTrap);
            carStat.put(MAX_MAXIMUM_SPEED, maxSpeed);
        };
    }

    private void resetData() {
        getCars().values().forEach(carStat -> {
            carStat.put(MAXIMUM_SPEED, 0);
            carStat.put(SPEED_TRAP_SPEED, 0);
        });
    }

}
