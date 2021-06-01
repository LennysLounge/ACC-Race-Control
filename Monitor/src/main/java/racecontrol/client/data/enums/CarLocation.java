/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data.enums;

/**
 *
 * @author Leonard
 */
public enum CarLocation {

    NONE(0),
    TRACK(1),
    PITLANE(2),
    PITENTRY(3),
    PITEXIT(4);

    private int id;

    private CarLocation(int id) {
        this.id = id;
    }

    public static CarLocation fromId(int id) {
        switch (id) {
            case 0:
                return NONE;
            case 1:
                return TRACK;
            case 2:
                return PITLANE;
            case 3:
                return PITENTRY;
            case 4:
                return PITEXIT;
            default:
                return NONE;
        }
    }

    public int getId() {
        return id;
    }
}
