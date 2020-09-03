/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.networking.enums.DriverCategory;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class ListEntry {

    private final String position;
    private final String name;
    private final String carNumber;
    private final String lapCount;
    private final String gap;
    private final String toLeader;
    private final String delta;
    private final String currentLap;
    private final String sectorOne;
    private final String sectorTwo;
    private final String sectorThree;
    private final String bestLap;
    private final String lastLap;

    private final boolean inPits;
    private final DriverCategory category;

    public ListEntry(String position, String name, String carNumber, String lapCount, String gap, String toLeader, String delta, String currentLap, String sectorOne, String sectorTwo, String sectorThree, String bestLap, String lastLap, boolean inPits, DriverCategory category) {
        this.position = position;
        this.name = name;
        this.carNumber = carNumber;
        this.lapCount = lapCount;
        this.gap = gap;
        this.toLeader = toLeader;
        this.delta = delta;
        this.currentLap = currentLap;
        this.sectorOne = sectorOne;
        this.sectorTwo = sectorTwo;
        this.sectorThree = sectorThree;
        this.bestLap = bestLap;
        this.lastLap = lastLap;
        this.inPits = inPits;
        this.category = category;
    }

    public String getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getLapCount() {
        return lapCount;
    }

    public String getGap() {
        return gap;
    }

    public String getToLeader() {
        return toLeader;
    }

    public String getDelta() {
        return delta;
    }

    public String getCurrentLap() {
        return currentLap;
    }

    public String getSectorOne() {
        return sectorOne;
    }

    public String getSectorTwo() {
        return sectorTwo;
    }

    public String getSectorThree() {
        return sectorThree;
    }

    public String getBestLap() {
        return bestLap;
    }

    public String getLastLap() {
        return lastLap;
    }

    public boolean isInPits() {
        return inPits;
    }

    public DriverCategory getCategory() {
        return category;
    }

}
