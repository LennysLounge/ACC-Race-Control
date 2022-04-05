/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.protocol.LapInfo;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.enums.CarLocation;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.extension.laptimes.LapCompletedEvent;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SECTOR_ONE;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;
import racecontrol.utility.TimeUtils;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SECTOR_ONE_CALC;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SECTOR_TWO_CALC;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SECTOR_THREE_CALC;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_SECTOR_TWO;
import racecontrol.client.model.Car;

/**
 *
 * @author Leonard
 */
public class SectorTimesProcessor
        extends StatisticsProcessor {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(SectorTimesProcessor.class.getName());
    /**
     * Track data.
     */
    private TrackData trackData;
    /**
     * Holds the previous spline position for each car in a map from car id to
     * spline position.
     */
    private final Map<Integer, Float> prevSplinePosition = new HashMap<>();

    /**
     * Fields used for Debugging purposes.
     */
    private final boolean enableLogging = false;
    private final Map<Integer, List<Tuple>> sectorSuggestions = new HashMap<>();
    private final List<Integer> s1Avg = new ArrayList<>();
    private final List<Integer> s2Avg = new ArrayList<>();

    public SectorTimesProcessor(Map<Integer, CarStatisticsWritable> cars) {
        super(cars);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof LapCompletedEvent) {
            if (enableLogging) {
                debugLog(((LapCompletedEvent) e).getCar());
            }
            onLapCompleted(((LapCompletedEvent) e).getCar());
        } else if (e instanceof TrackDataEvent) {
            trackData = ((TrackDataEvent) e).getTrackData();
            s1Avg.clear();
            s2Avg.clear();
            getClient().getModel().cars.values().stream()
                    .map(car -> getCars().get(car.id))
                    .forEach(carStat -> {
                        carStat.put(CURRENT_SECTOR_ONE_CALC, 0);
                        carStat.put(CURRENT_SECTOR_TWO_CALC, 0);
                        carStat.put(CURRENT_SECTOR_THREE_CALC, 0);
                    });
        }
    }

    private void onRealtimeCarUpdate(RealtimeInfo info) {
        CarStatisticsWritable car = getCars().get(info.getCarId());
        // set sectors to zero if the state is invalid.
        if (trackData == null
                || !prevSplinePosition.containsKey(info.getCarId())
                || info.getLocation() != CarLocation.TRACK) {
            car.put(CURRENT_SECTOR_ONE_CALC, 0);
            car.put(CURRENT_SECTOR_TWO_CALC, 0);
            car.put(CURRENT_SECTOR_THREE_CALC, 0);
        } else {
            if (sectorOneCrossed(info)) {
                int s1Time = info.getCurrentLap().getLapTimeMS();
                float overshoot = trackData.getTrackMeters() * (info.getSplinePosition() - trackData.getSectorOneLine());
                float speedMS = info.getKMH() / 3.6f;
                int correction = (int) ((overshoot / speedMS) * 1000f);
                car.put(CURRENT_SECTOR_ONE_CALC, s1Time - correction);
                car.put(CURRENT_SECTOR_TWO_CALC, 0);
                car.put(CURRENT_SECTOR_THREE_CALC, 0);
                car.put(CURRENT_SECTOR_ONE, s1Time - correction);
                car.put(CURRENT_SECTOR_TWO, 0);
                car.put(CURRENT_SECTOR_THREE, 0);
            }
            if (sectorTwoCrossed(info)) {
                int s2Time = info.getCurrentLap().getLapTimeMS() - car.get(CURRENT_SECTOR_ONE_CALC);
                float overshoot = trackData.getTrackMeters() * (info.getSplinePosition() - trackData.getSectorTwoLine());
                float speedMS = info.getKMH() / 3.6f;
                int correction = (int) ((overshoot / speedMS) * 1000f);
                car.put(CURRENT_SECTOR_TWO_CALC, s2Time - correction);
                car.put(CURRENT_SECTOR_THREE_CALC, 0);
                car.put(CURRENT_SECTOR_TWO, s2Time - correction);
                car.put(CURRENT_SECTOR_THREE, 0);
            }
        }
        prevSplinePosition.put(info.getCarId(), info.getSplinePosition());

        if (!sectorSuggestions.containsKey(info.getCarId())) {
            sectorSuggestions.put(info.getCarId(), new ArrayList<>());
        }
        Tuple t = new Tuple();
        t.splinePos = info.getSplinePosition();
        t.time = info.getCurrentLap().getLapTimeMS();
        sectorSuggestions.get(info.getCarId()).add(t);
    }

    private void onLapCompleted(Car car) {
        CarStatisticsWritable carStats = getCars().get(car.id);
        int lapTime = car.lastLap.getLapTimeMS();
        int s3Time = lapTime - carStats.get(CURRENT_SECTOR_ONE_CALC) - carStats.get(CURRENT_SECTOR_TWO_CALC);
        carStats.put(CURRENT_SECTOR_THREE_CALC, s3Time);

        LapInfo lastLap = car.lastLap;
        carStats.put(CURRENT_SECTOR_ONE, lastLap.getSplits().get(0));
        carStats.put(CURRENT_SECTOR_TWO, lastLap.getSplits().get(1));
        carStats.put(CURRENT_SECTOR_THREE, lastLap.getSplits().get(2));
    }

    private boolean sectorOneCrossed(RealtimeInfo info) {
        return prevSplinePosition.get(info.getCarId()) < trackData.getSectorOneLine()
                && info.getSplinePosition() > trackData.getSectorOneLine();
    }

    private boolean sectorTwoCrossed(RealtimeInfo info) {
        return prevSplinePosition.get(info.getCarId()) < trackData.getSectorTwoLine()
                && info.getSplinePosition() > trackData.getSectorTwoLine();
    }

    private void debugLog(Car car) {
        if (trackData != null) {
            CarStatisticsWritable carStats = getCars().get(car.id);
            LapInfo lastLap = car.lastLap;
            // Sector suggestions.
            if (sectorSuggestions.containsKey(car.id)) {
                int s1Time = lastLap.getSplits().get(0);
                for (Tuple t : sectorSuggestions.get(car.id)) {
                    if (t.time > s1Time) {
                        LOG.info(String.format("S1 suggestion: %.7f\t%s",
                                t.splinePos,
                                TimeUtils.asDelta(t.time - s1Time)
                        ));
                        break;
                    }
                }
                int s2Time = lastLap.getSplits().get(1) + s1Time;
                for (Tuple t : sectorSuggestions.get(car.id)) {
                    if (t.time > s2Time) {
                        LOG.info(String.format("S2 suggestion: %.7f\t%s",
                                t.splinePos,
                                TimeUtils.asDelta(t.time - s2Time)
                        ));
                        break;
                    }
                }
                sectorSuggestions.get(car.id).clear();
            }

            int s1Diff = carStats.get(CURRENT_SECTOR_ONE_CALC) - lastLap.getSplits().get(0);
            int s2Diff = carStats.get(CURRENT_SECTOR_TWO_CALC) - lastLap.getSplits().get(1);
            if (carStats.get(CURRENT_SECTOR_TWO_CALC) != 0
                    && Math.abs(s2Diff) < 10000
                    && carStats.get(CURRENT_SECTOR_ONE_CALC) != 0
                    && Math.abs(s1Diff) < 10000) {

                LOG.info(String.format("S1: %s\t%s\t%s",
                        TimeUtils.asSeconds(lastLap.getSplits().get(0)),
                        TimeUtils.asSeconds(carStats.get(CURRENT_SECTOR_ONE_CALC)),
                        TimeUtils.asDelta(s1Diff)
                ));
                LOG.info(String.format("S2: %s\t%s\t%s",
                        TimeUtils.asSeconds(lastLap.getSplits().get(1)),
                        TimeUtils.asSeconds(carStats.get(CURRENT_SECTOR_TWO_CALC)),
                        TimeUtils.asDelta(s2Diff)
                ));

                s1Avg.add(s1Diff);
                s2Avg.add(s2Diff);
                LOG.info(String.format("S1 avg: %s\tS2 avg:%s",
                        TimeUtils.asDelta((int) average(s1Avg)),
                        TimeUtils.asDelta((int) average(s2Avg))
                ));
            }
        }
    }

    private float average(List<Integer> list) {
        float sum = 0;
        for (int v : list) {
            sum += v;
        }
        return sum / list.size();
    }

    private class Tuple {

        float splinePos;
        int time;
    }
}
