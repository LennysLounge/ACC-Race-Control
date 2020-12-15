/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.data;

import base.screen.networking.enums.LapType;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class LapInfo {

    int lapTimeMS;
    int carId;
    int driverIndex;
    List<Integer> splits = new LinkedList<>();
    boolean isInvalid;
    boolean isValidForBest;
    LapType type = LapType.ERROR;

    public LapInfo() {
    }

    public LapInfo(int lapTimeMS, int carIndex, int driverIndex, List<Integer> splits, boolean isInvalid,
            boolean isValidForBest, LapType type) {
        this.lapTimeMS = lapTimeMS;
        this.carId = carIndex;
        this.driverIndex = driverIndex;
        this.splits = splits;
        this.isInvalid = isInvalid;
        this.isValidForBest = isValidForBest;
        this.type = type;
    }

    public int getLapTimeMS() {
        return lapTimeMS;
    }

    public int getCarId() {
        return carId;
    }

    public int getDriverIndex() {
        return driverIndex;
    }

    public List<Integer> getSplits() {
        return splits;
    }

    public boolean getIsInvalid() {
        return isInvalid;
    }

    public boolean getIsValidForBest() {
        return isValidForBest;
    }

    public LapType getType() {
        return type;
    }

}
