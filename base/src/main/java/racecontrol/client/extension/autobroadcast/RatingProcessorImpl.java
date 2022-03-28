/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_BEHIND;
import static racecontrol.client.extension.statistics.CarProperties.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarProperties.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_COMPLEX;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class RatingProcessorImpl
        implements RatingProcessor {

    private int currentFocusId = -1;
    private long focusChangedTimeStamp = 0;

    public RatingProcessorImpl() {
    }

    @Override
    public Entry calculateRating(Entry entry) {
        CarStatistics stats = StatisticsExtension.getInstance()
                .getCar(entry.getCarInfo().getCarId());

        if (stats.get(IS_IN_PITS)) {
            return entry;
        }

        // gap to car ahead linearly scaled from 1.5s to 0
        float proximityFront = 1 - clamp(stats.get(GAP_TO_POSITION_AHEAD) / 2500f);
        entry.setProximityFont(proximityFront);

        // gap to car behind, linearly scaled from 1.5s to 0
        float proximityBehind = 1 - clamp(stats.get(GAP_TO_POSITION_BEHIND) / 2500f);
        entry.setProximityRear(proximityBehind);

        // overall proximity is the bigger proximity either ahead or behind.
        // The proximity front of this car is equal to proximity behind of the 
        // car ahead. To avoid this we bias either front or rear proximity to
        // priorytise either the front or rear driver.
        float frontBias = 1f;
        float rearBias = 0.95f;
        if (Calendar.getInstance().get(Calendar.MINUTE) % 2 == 0) {
            frontBias = 0.95f;
            rearBias = 1f;
        }
        entry.setProximity(Math.max(
                proximityFront * frontBias, proximityBehind * rearBias));

        // Position rating. Having a higher position gives a better rating.
        // scaled from p1 to last place linearly.
        float position = stats.get(REALTIME_POSITION) * 1f
                / getClient().getModel().getCarsInfo().size() * 1f;
        entry.setPosition(1 - position);

        // Focus changed penalty. When the focus changes, cars that are not in
        // focus get a rating penatly to avoid fast switching.
        if (stats.get(IS_FOCUSED_ON)) {
            entry.setFocusFast(1f);
            entry.setFocusSlow(1f);
        } else {
            int msSinceFocusChange = (int) (System.currentTimeMillis() - focusChangedTimeStamp);
            entry.setFocusFast(clamp(msSinceFocusChange / 10000f));
            entry.setFocusSlow(clamp(msSinceFocusChange / 60000f));
        }

        // pack rating. The more cars are close on track the higher.
        // Scaled linearly from form 5 cars withing 2.5 seconds to 0.
        int count = 0;
        List<CarStatistics> carsSorted = getClient().getModel().getCarsInfo().values().stream()
                .map(carInfo -> StatisticsExtension.getInstance().getCar(carInfo.getCarId()))
                .sorted((c1, c2) -> Float.compare(c2.get(RACE_DISTANCE_COMPLEX), c1.get(RACE_DISTANCE_COMPLEX)))
                .collect(Collectors.toList());
        int index = -1;
        for (var car : carsSorted) {
            index++;
            if (Objects.equals(car.get(CAR_ID), stats.get(CAR_ID))) {
                break;
            }
        }
        // looking 2.5 seconds backwards
        int countBack = 0;
        int distance = stats.get(GAP_TO_POSITION_BEHIND);
        int i = index + 1;
        while (distance < 2500 && i < carsSorted.size()) {
            countBack++;
            distance += carsSorted.get(i).get(GAP_TO_POSITION_BEHIND);
            i++;
        }
        // looking 2.5 seconds ahead
        int countFront = 0;
        distance = stats.get(GAP_TO_POSITION_AHEAD);
        i = index - 1;
        while (distance < 2500 && i > 0) {
            countFront++;
            distance += carsSorted.get(i).get(GAP_TO_POSITION_AHEAD);
            i--;
        }
        entry.setPackBack(countBack);
        entry.setPackFront(countFront);
        entry.setPack(clamp((countBack + countFront) / 5f));

        return entry;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    public void sessionUpdate(SessionInfo info) {
        if (info.getFocusedCarIndex() != currentFocusId) {
            currentFocusId = info.getFocusedCarIndex();
            focusChangedTimeStamp = System.currentTimeMillis();
        }
    }

    private float clamp(float v) {
        return Math.max(0, Math.min(1, v));
    }

}
