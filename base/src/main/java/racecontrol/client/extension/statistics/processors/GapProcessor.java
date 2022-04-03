/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_CAR_AHEAD;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_CAR_BEHIND;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_LEADER;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_POSITION_BEHIND;
import static racecontrol.client.extension.statistics.CarStatistics.LAPS_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarStatistics.LAPS_BEHIND_SPLIT;
import static racecontrol.client.extension.statistics.CarStatistics.LAP_TIME_GAP_TO_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarStatistics.RACE_DISTANCE_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import racecontrol.client.model.Car;

/**
 *
 * @author Leonard
 */
public class GapProcessor extends StatisticsProcessor {

    private final AccBroadcastingClient client;

    private TrackData trackData;

    private final GapCalculator gapCalculator = new GapCalculator();

    public GapProcessor(Map<Integer, CarStatisticsWritable> cars) {
        super(cars);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            onRealtimeUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof TrackDataEvent) {
            trackData = ((TrackDataEvent) e).getTrackData();
            gapCalculator.setTrackData(trackData);
        }
    }

    public void onRealtimeUpdate(SessionInfo info) {

        //calculate gap to session best lap time.
        for (CarStatisticsWritable car : getCars().values()) {
            LapInfo sessionBestLap = info.getBestSessionLap();
            int diff = car.get(BEST_LAP_TIME) - sessionBestLap.getLapTimeMS();
            car.put(LAP_TIME_GAP_TO_SESSION_BEST, diff);
        }

        // Skip if we dont know the track or the v map.
        if (trackData == null
                || trackData.getGt3VelocityMap().isEmpty()
                || client.getModel().cars.isEmpty()) {
            for (CarStatisticsWritable car : getCars().values()) {
                if (trackData == null
                        || trackData.getGt3VelocityMap().isEmpty()) {
                    car.put(GAP_TO_LEADER, 0);
                    car.put(GAP_TO_POSITION_AHEAD, 0);
                    car.put(GAP_TO_CAR_AHEAD, 0);
                    car.put(LAPS_BEHIND_LEADER, 0);
                    car.put(LAPS_BEHIND_SPLIT, false);
                    car.put(RACE_DISTANCE_BEHIND_LEADER, 0f);
                }
            }
            return;
        }

        // calculate gaps to position ahead and leader.
        List<Car> cars = client.getModel().cars.values().stream()
                .sorted((c1, c2) -> {
                    return getCars().get(c1.raw.getCarId()).get(REALTIME_POSITION)
                            .compareTo(getCars().get(c2.raw.getCarId()).get(REALTIME_POSITION));
                })
                .collect(Collectors.toList());
        float leaderRaceDistance = cars.get(0).realtimeRaw.getLaps()
                + cars.get(0).realtimeRaw.getSplinePosition();
        int splitLapsBehind = 0;
        CarStatisticsWritable carStats = getCars().get(cars.get(0).raw.getCarId());
        carStats.put(GAP_TO_LEADER, 0);
        carStats.put(GAP_TO_POSITION_AHEAD, Integer.MAX_VALUE);
        carStats.put(LAPS_BEHIND_LEADER, 0);
        carStats.put(LAPS_BEHIND_SPLIT, false);
        carStats.put(RACE_DISTANCE_BEHIND_LEADER, 0f);
        for (int i = 1; i < cars.size(); i++) {
            int gap = (int) gapCalculator.calculateGap(cars.get(i), cars.get(i - 1));
            int gapToLeader = (int) gapCalculator.calculateGap(cars.get(i), cars.get(0));

            float raceDistance = cars.get(i).realtimeRaw.getLaps()
                    + cars.get(i).realtimeRaw.getSplinePosition();
            int lapsBehind = (int) Math.floor(leaderRaceDistance - raceDistance);

            carStats = getCars().get(cars.get(i).raw.getCarId());
            carStats.put(GAP_TO_LEADER, gapToLeader);
            carStats.put(GAP_TO_POSITION_AHEAD, gap);

            carStats.put(LAPS_BEHIND_LEADER, lapsBehind);
            carStats.put(LAPS_BEHIND_SPLIT, lapsBehind > splitLapsBehind);
            carStats.put(RACE_DISTANCE_BEHIND_LEADER, leaderRaceDistance - raceDistance);

            splitLapsBehind = lapsBehind;
        }

        // gap to position behind
        for (int i = 0; i < cars.size() - 1; i++) {
            carStats = getCars().get(cars.get(i).raw.getCarId());
            int gap = (int) gapCalculator.calculateGap(cars.get(i + 1), cars.get(i));
            carStats.put(GAP_TO_POSITION_BEHIND, gap);
        }
        carStats = getCars().get(cars.get(cars.size() - 1).raw.getCarId());
        carStats.put(GAP_TO_POSITION_BEHIND, Integer.MAX_VALUE);

        // cap to cars ahead / behind
        cars = client.getModel().cars.values().stream()
                .sorted((c1, c2) -> Float.compare(c1.realtimeRaw.getSplinePosition(), c2.realtimeRaw.getSplinePosition()))
                .collect(Collectors.toList());
        for (int i = 0; i < cars.size(); i++) {
            int next = (i == cars.size() - 1) ? 0 : i + 1;
            int gapToCar = (int) gapCalculator.calculateGap(cars.get(i), cars.get(next));

            carStats = getCars().get(cars.get(i).raw.getCarId());
            carStats.put(GAP_TO_CAR_AHEAD, gapToCar);
        }
        for (int i = 0; i < cars.size(); i++) {
            int prev = (i == 0) ? cars.size() - 1 : i - 1;
            int gapToCar = (int) gapCalculator.calculateGap(cars.get(prev), cars.get(i));

            carStats = getCars().get(cars.get(i).raw.getCarId());
            carStats.put(GAP_TO_CAR_BEHIND, gapToCar);
        }

    }

}
