/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import racecontrol.client.data.CarInfo;
import racecontrol.client.model.Car;

/**
 *
 * @author Leonard
 */
public class YellowFlagContactInfo {

    /**
     * The car that has been yellow flagged.
     */
    private final Car flaggedCar;
    /**
     * The car that is closest when the flag was raised.
     */
    private final Car closestCar;
    /**
     * Session time when the yellow flagg was raised.
     */
    private final int sessionTime;
    /**
     * Event if of the original yellow flag event.
     */
    private final int yellowFlagEventId;

    public YellowFlagContactInfo(Car flaggedCar,
            Car closestCar,
            int sessionTime,
            int yellowFlagEventId) {
        this.flaggedCar = flaggedCar;
        this.closestCar = closestCar;
        this.sessionTime = sessionTime;
        this.yellowFlagEventId = yellowFlagEventId;
    }

    public Car getFlaggedCar() {
        return flaggedCar;
    }

    public Car getClosestCar() {
        return closestCar;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public int getYellowFlagEventId() {
        return yellowFlagEventId;
    }
}
