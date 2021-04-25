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
    
    private CarInfo carInfo;
    
    public LiveTimingEntry(CarInfo carInfo){
        this.carInfo = carInfo;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }    
}
