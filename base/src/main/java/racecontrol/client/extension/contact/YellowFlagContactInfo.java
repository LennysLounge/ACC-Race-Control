/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import racecontrol.client.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class YellowFlagContactInfo {

    /**
     * The car that has been yellow flagged.
     */
    private final CarInfo flaggedCar;
    /**
     * The car that is closest when the flag was raised.
     */
    private final CarInfo closestCar;
    /**
     * Session time when the yellow flagg was raised.
     */
    private final int sessionTime;
    /**
     * Event if of the original yellow flag event.
     */
    private final int yellowFlagEventId;

    public YellowFlagContactInfo(CarInfo flaggedCar,
            CarInfo closestCar,
            int sessionTime,
            int yellowFlagEventId) {
        this.flaggedCar = flaggedCar;
        this.closestCar = closestCar;
        this.sessionTime = sessionTime;
        this.yellowFlagEventId = yellowFlagEventId;
    }

    public CarInfo getFlaggedCar() {
        return flaggedCar;
    }

    public CarInfo getClosestCar() {
        return closestCar;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public int getYellowFlagEventId() {
        return yellowFlagEventId;
    }
}
