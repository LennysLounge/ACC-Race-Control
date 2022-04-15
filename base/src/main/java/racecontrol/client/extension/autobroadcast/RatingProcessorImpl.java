/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.model.Car;
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
    public CarRating calculateRating(CarRating entry) {
        Car car = entry.car;

        if (!isCarValidForRating(car)) {
            return entry;
        }

        // proximity rating. Battles with multiple cars are more desirable.
        // each car within 2.5 seconds adds their proportinal ammount to the
        // rating.
        float proximity = 0;
        Car currentCar = car;
        int gap = 0;
        while (currentCar.carPositionAhead != 0) {
            // move to car ahead.
            currentCar = getClient().getModel().cars.get(currentCar.carPositionAhead);
            gap += currentCar.gapPositionBehind;
            if (gap > 2500) {
                break;
            }
            if (isCarValidForRating(currentCar)) {
                proximity += Math.pow(1 - (gap / 2500f), 2);
            }
        }
        currentCar = car;
        gap = 0;
        while (currentCar.carPositionBehind != 0) {
            // move to car behind
            currentCar = getClient().getModel().cars.get(currentCar.carPositionBehind);
            gap += currentCar.gapPositionAhead;
            if (gap > 2500) {
                break;
            }
            if (isCarValidForRating(currentCar)) {
                proximity += Math.pow(1 - (gap / 2500f), 2);
            }
        }
        entry.proximity = proximity;

        // Position rating. Fights in higher positions are more desirable.
        // sort cars by their race position from first to last.
        int carCount = (int) getClient().getModel().cars.values().stream()
                .filter(c -> c.connected)
                .count();
        entry.position = 1 - (car.realtimePosition * 1f / carCount * 1f);

        // Focus changed penalty. When the focus changes, cars that are not in
        // focus get a rating penatly to avoid fast switching.
        if (car.isFocused) {
            entry.focus = 1f;
        } else {
            int msSinceFocusChange = (int) (System.currentTimeMillis() - focusChangedTimeStamp);
            entry.focus = clamp(msSinceFocusChange / 60000f);
        }

        // Tie braker: a very small value used to brake ties.
        // For now it scales with the position from P1=0.001 to 0
        entry.tieBraker = entry.position * 0.001f;

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

    private boolean isCarValidForRating(Car car) {
        return !car.isInPit()
                && !car.isCheckeredFlag
                && !car.isYellowFlag
                && !car.isWhiteFlag;
    }

}
