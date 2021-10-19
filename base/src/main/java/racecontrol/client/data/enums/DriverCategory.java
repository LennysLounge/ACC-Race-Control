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
public enum DriverCategory {

    BRONZE(0, "Bronze"),
    SILVER(1, "Silver"),
    GOLD(2, "Gold"),
    PLATINUM(3, "Platinum"),
    ERROR(255, "Error");

    private int id;
    private String text;

    private DriverCategory(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public static DriverCategory fromId(int id) {
        switch (id) {
            case 0:
                return BRONZE;
            case 1:
                return SILVER;
            case 2:
                return GOLD;
            case 3:
                return PLATINUM;
            default:
                return ERROR;
        }
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}
