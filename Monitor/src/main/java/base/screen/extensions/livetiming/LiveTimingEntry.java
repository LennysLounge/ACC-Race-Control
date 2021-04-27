/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.screen.networking.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class LiveTimingEntry {

    private final CarInfo carInfo;

    private final float raceDistance;

    private float gap;

    private float gapToLeader;

    private boolean showLapsBehind;

    private int lapsBehind;

    public LiveTimingEntry(CarInfo carInfo,
            float raceDistance) {
        this(carInfo, raceDistance, 0, 0, false, 0);
    }

    public LiveTimingEntry(CarInfo carInfo,
            float raceDistance,
            float lapTime,
            float gapToLeader,
            boolean isLapBehind,
            int lapsBehind) {
        this.carInfo = carInfo;
        this.raceDistance = raceDistance;
        this.gap = lapTime;
        this.gapToLeader = gapToLeader;
        this.showLapsBehind = isLapBehind;
        this.lapsBehind = lapsBehind;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public float getGap() {
        return gap;
    }

    public float getGapToLeader() {
        return gapToLeader;
    }

    public boolean showLapsBehind() {
        return showLapsBehind;
    }

    public int getLapsBehind() {
        return lapsBehind;
    }

    public float getRaceDistance() {
        return raceDistance;
    }

}
