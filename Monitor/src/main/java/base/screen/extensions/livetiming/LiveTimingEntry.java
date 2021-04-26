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
    
    private float naiveLapTime;
    private float lapTime;

    public LiveTimingEntry(CarInfo carInfo) {
        this(carInfo, 0, 0);
    }

    public LiveTimingEntry(CarInfo carInfo,
            float naiveLapTime,
            float lapTime) {
        this.carInfo = carInfo;
        this.naiveLapTime = naiveLapTime;
        this.lapTime = lapTime;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public float getNaiveLapTime() {
        return naiveLapTime;
    }

    public float getLapTime() {
        return lapTime;
    }
    
    


}
