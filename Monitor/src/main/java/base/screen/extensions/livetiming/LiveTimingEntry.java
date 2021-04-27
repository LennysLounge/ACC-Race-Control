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

    private float gap;

    private float total;

    private boolean showLapsBehind;
    
    private int lapsBehind;

    public LiveTimingEntry(CarInfo carInfo) {
        this(carInfo, 0, 0, false, 0);
    }

    public LiveTimingEntry(CarInfo carInfo,
            float lapTime,
            float total,
            boolean isLapBehind,
            int lapsBehind) {
        this.carInfo = carInfo;
        this.gap = lapTime;
        this.total = total;
        this.showLapsBehind = isLapBehind;
        this.lapsBehind = lapsBehind;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public float getGap() {
        return gap;
    }

    public float getTotal() {
        return total;
    }

    public boolean showLapsBehind() {
        return showLapsBehind;
    }
    
    public int getLapsBehind(){
        return lapsBehind; 
   }

}
