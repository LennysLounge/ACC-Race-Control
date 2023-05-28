/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol;

import java.util.Arrays;
import racecontrol.client.protocol.enums.LapType;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class LapInfo {

    int lapTimeMS;
    int carId;
    int driverIndex;
    List<Integer> splits = Arrays.asList(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
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

    public boolean isInvalid() {
        return isInvalid;
    }

    public boolean getIsValidForBest() {
        return isValidForBest;
    }

    public LapType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "LapInfo{"
                + "lapTimeMS=" + lapTimeMS
                + ", carId=" + carId
                + ", driverIndex=" + driverIndex
                + ", splits=" + splits
                + ", isInvalid=" + isInvalid
                + ", isValidForBest=" + isValidForBest
                + ", type=" + type
                + '}';
    }

}
