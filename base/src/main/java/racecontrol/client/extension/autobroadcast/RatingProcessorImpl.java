/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_ID;
import static racecontrol.client.extension.statistics.CarStatistics.DELTA;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_POSITION_BEHIND;
import static racecontrol.client.extension.statistics.CarStatistics.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarStatistics.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarStatistics.PREDICTED_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.SPLINE_POS;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class RatingProcessorImpl
        implements RatingProcessor {

    /**
     * The car with the current focus
     */
    private int focusedCarId = -1;
    /**
     * Timestamp for when the focus last changed.
     */
    private long focusChangedTimeStamp = 0;
    /**
     * Whether the focused car has crossed the finish line.
     */
    private boolean focusedCarCrossedFinish = false;
    /**
     * Spline position of the focused car.
     */
    private float focusedCarSplinePos = 0f;

    public RatingProcessorImpl() {
    }

    @Override
    public Entry calculateRating(Entry entry) {
        CarStatistics stats = StatisticsExtension.getInstance()
                .getCar(entry.getCarInfo().getCarId());
        // sort cars by their race position from first to last.
        List<CarStatistics> carsSorted = getClient().getModel().cars.values().stream()
                .map(car -> car.raw)
                .map(carInfo -> StatisticsExtension.getInstance().getCar(carInfo.getCarId()))
                .sorted((c1, c2) -> Float.compare(c1.get(REALTIME_POSITION), c2.get(REALTIME_POSITION)))
                .collect(Collectors.toList());
        int index = stats.get(REALTIME_POSITION) - 1;

        if (index < 0 || index >= carsSorted.size()) {
            return entry;
        }

        if (!carsSorted.get(index).get(CAR_ID).equals(stats.get(CAR_ID))) {
            return entry;
        }

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
                / getClient().getModel().cars.size() * 1f;
        entry.setPosition(1 - position);

        // Focus changed penalty. When the focus changes, cars that are not in
        // focus get a rating penatly to avoid fast switching.
        if (stats.get(IS_FOCUSED_ON)) {
            entry.setFocusFast(1f);
            entry.setFocusSlow(1f);
            entry.setFocus(1f);
        } else {
            int msSinceFocusChange = (int) (System.currentTimeMillis() - focusChangedTimeStamp);
            entry.setFocusFast(clamp(msSinceFocusChange / 10000f));
            entry.setFocusSlow(clamp(msSinceFocusChange / 60000f));
            entry.setFocus(clamp(msSinceFocusChange / 60000f));
        }

        // pack rating. The more cars are close on track the higher.
        // Scaled linearly from form 5 cars withing 2.5 seconds to 0.
        int count = 0;
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

        // pack proximity rating.
        // Adds up proximity ratings for every car within 2.5 seconds.
        float packProximityRating = 0;
        // looking backwards
        int currentIndex = index + 1;
        distance = stats.get(GAP_TO_POSITION_BEHIND);
        while (distance < 2500 && currentIndex < carsSorted.size()) {
            packProximityRating += 1 - distance / 2500f;
            distance += carsSorted.get(currentIndex++).get(GAP_TO_POSITION_AHEAD);
        }
        // looking forwards
        currentIndex = index - 1;
        distance = stats.get(GAP_TO_POSITION_AHEAD);
        while (distance < 2500 && currentIndex > 0) {
            packProximityRating += 1 - distance / 2500f;
            distance += carsSorted.get(currentIndex--).get(GAP_TO_POSITION_AHEAD);
        }
        entry.setPackProximity(packProximityRating);

        // Pace rating. Quick predicted lap times give a higher rating.
        // Session best has priority.
        float deltaPace = 1 - clamp(stats.get(DELTA) / -500f);
        int sessionBestDif = stats.get(PREDICTED_LAP_TIME) - stats.get(SESSION_BEST_LAP_TIME);
        float sessionBestPace = 1 - clamp(sessionBestDif / 1000f);
        entry.setPace(Math.max(deltaPace / 2, sessionBestPace));

        // Pace focus. When we are spectating a lap we want to keep looking at
        // that lap until it is invalidated or done.
        if (stats.get(IS_FOCUSED_ON)) {
            entry.setPaceFocus(1f);
        } else {
            CarStatistics focusedCar = StatisticsExtension.getInstance().getCar(focusedCarId);
            // we ignore the first 20% of a lap
            if (stats.get(SPLINE_POS) < 0.2) {
                entry.setPaceFocus(0f);
            } else {
                if (focusedCarCrossedFinish) {
                    entry.setPaceFocus(1f);
                } else {
                    entry.setPaceFocus(0);
                }
            }
        }

        entry.setRandomness((float) Math.random() * 0.001f);

        return entry;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            sessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdateEvent) {
            RealtimeInfo info = ((RealtimeCarUpdateEvent) e).getInfo();
            if (info.getCarId() == focusedCarId) {
                if (info.getSplinePosition() < focusedCarSplinePos) {
                    focusedCarCrossedFinish = true;
                }
                focusedCarSplinePos = info.getSplinePosition();
            }
        }
    }

    public void sessionUpdate(SessionInfo info) {
        if (info.getFocusedCarIndex() != focusedCarId) {
            focusedCarId = info.getFocusedCarIndex();
            focusChangedTimeStamp = System.currentTimeMillis();
            focusedCarCrossedFinish = false;
            focusedCarSplinePos = 0;
        }

    }

    private float clamp(float v) {
        return Math.max(0, Math.min(1, v));
    }

}
