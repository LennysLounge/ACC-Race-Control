/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.racereport;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class DriverRecord {

    private final String driverName;
    private final String carNumber;
    private final List<LapRecord> laps = new ArrayList<>();
    private int position;
    private int lapCount;

    public DriverRecord(String driverName, String carNumber) {
        this.driverName = driverName;
        this.carNumber = carNumber;
    }
    
    public void setPosition(int pos){
        position = pos;
    }
    
    public int getPosition(){
        return position;
    }

    public int getLapCount() {
        return lapCount;
    }

    public void setLapCount(int lapCount) {
        this.lapCount = lapCount;
    }
    
    

    public String getDriverName() {
        return driverName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public List<LapRecord> getLaps() {
        return laps;
    }

}
