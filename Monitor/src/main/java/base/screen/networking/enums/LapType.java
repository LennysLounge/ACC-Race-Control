/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking.enums;

/**
 *
 * @author Leonard
 */
public enum LapType {

    ERROR(0),
    OUTLAP(1),
    REGULAR(2),
    INLAP(3);

    private int id;

    private LapType(int id) {
        this.id = id;
    }

    public static LapType fromId(int id) {
        switch (id) {
            case 0:
                return ERROR;
            case 1:
                return OUTLAP;
            case 2:
                return REGULAR;
            case 3:
                return INLAP;
            default:
                return ERROR;
        }
    }

    public int getId() {
        return id;
    }

}
