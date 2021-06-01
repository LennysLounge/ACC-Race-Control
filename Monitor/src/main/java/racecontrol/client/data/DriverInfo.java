/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data;

import racecontrol.client.data.enums.DriverCategory;
import racecontrol.client.data.enums.Nationality;

/**
 *
 * @author Leonard
 */
public class DriverInfo {

    private String firstName = "";
    private String lastName = "";
    private String shortName = "";
    private DriverCategory category = DriverCategory.ERROR;
    private Nationality driverNationality;

    public DriverInfo() {
    }

    public DriverInfo(String firstName, String lastName, String shortName, DriverCategory category, Nationality driverNationality) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.shortName = shortName;
        this.category = category;
        this.driverNationality = driverNationality;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getShortName() {
        return shortName;
    }

    public DriverCategory getCategory() {
        return category;
    }

    public Nationality getDriverNationality() {
        return driverNationality;
    }

}
