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
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_CAR_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_SPLIT;
import static racecontrol.client.extension.statistics.CarProperties.LAP_TIME_GAP_TO_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_BEHIND;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class GapProcessor extends StatisticsProcessor {

    private final AccBroadcastingClient client;

    private TrackData trackData;

    private final GapCalculator gapCalculator = new GapCalculator();

    public GapProcessor(Map<Integer, WritableCarStatistics> cars) {
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
        for (WritableCarStatistics car : getCars().values()) {
            LapInfo sessionBestLap = info.getBestSessionLap();
            int diff = car.get(BEST_LAP_TIME) - sessionBestLap.getLapTimeMS();
            car.put(LAP_TIME_GAP_TO_SESSION_BEST, diff);

            if (trackData == null
                    || trackData.getGt3VelocityMap().isEmpty()) {
                car.put(GAP_TO_LEADER, 0);
                car.put(GAP_TO_POSITION_AHEAD, 0);
                car.put(GAP_TO_CAR_AHEAD, 0);
            }
        }

        // Skip if we dont know the track or the v map.
        if (trackData == null
                || trackData.getGt3VelocityMap().isEmpty()) {
            return;
        }
        // calculate gaps to position ahead and leader.
        List<CarInfo> cars = client.getModel().getCarsInfo().values().stream()
                .sorted((c1, c2) -> c1.getRealtime().getPosition() - c2.getRealtime().getPosition())
                .collect(Collectors.toList());
        float leaderRaceDistance = cars.get(0).getRealtime().getLaps()
                + cars.get(0).getRealtime().getSplinePosition();
        int splitLapsBehind = 0;
        for (int i = 1; i < cars.size(); i++) {
            int gap = (int) gapCalculator.calculateGap(cars.get(i), cars.get(i - 1));
            int gapToLeader = (int) gapCalculator.calculateGap(cars.get(i), cars.get(0));

            float raceDistance = cars.get(i).getRealtime().getLaps()
                    + cars.get(i).getRealtime().getSplinePosition();
            int lapsBehind = (int) Math.floor(leaderRaceDistance - raceDistance);

            WritableCarStatistics car = getCars().get(cars.get(i).getCarId());
            car.put(GAP_TO_LEADER, gapToLeader);
            car.put(GAP_TO_POSITION_AHEAD, gap);
            car.put(LAPS_BEHIND_LEADER, lapsBehind);
            car.put(LAPS_BEHIND_SPLIT, lapsBehind > splitLapsBehind);
            car.put(RACE_DISTANCE_BEHIND, leaderRaceDistance - raceDistance);

            splitLapsBehind = lapsBehind;
        }

        for (WritableCarStatistics car : getCars().values()) {
            car.put(GAP_TO_CAR_AHEAD, 0);
        }
    }

}
