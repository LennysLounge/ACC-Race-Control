/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.livetiming;

import racecontrol.client.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class LiveTimingEntry {

    private final CarInfo carInfo;

    private float gap;

    private float gapToLeader;

    private boolean showLapsBehind;

    private int lapsBehind;

    public LiveTimingEntry(CarInfo carInfo) {
        this(carInfo, 0, 0, false, 0);
    }

    public LiveTimingEntry(CarInfo carInfo,
            float lapTime,
            float gapToLeader,
            boolean isLapBehind,
            int lapsBehind) {
        this.carInfo = carInfo;
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

}
