/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import racecontrol.client.protocol.enums.DriverCategory;
import racecontrol.client.protocol.enums.Nationality;

/**
 *
 * @author Leonard
 */
public class Driver {

    /**
     * First name of the driver.
     */
    public String firstName = "";
    /**
     * Last name of the driver.
     */
    public String lastName = "";
    /**
     * Short name of the driver.
     */
    public String shortName = "";
    /**
     * Driver category.
     */
    public DriverCategory category = DriverCategory.ERROR;
    /**
     * Driver nationality.
     */
    public Nationality nationality;

    /**
     * Returns the full name of the driver.
     *
     * @return the full name of the driver.
     */
    public String fullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns the full name with a truncated first name.
     *
     * @return the full name with a truncated first name.
     */
    public String truncatedName() {
        return firstName.substring(0, Math.min(firstName.length(), 1))
                + ". " + lastName;
    }

    public synchronized Driver copy() {
        var driver = new Driver();
        driver.firstName = firstName;
        driver.lastName = lastName;
        driver.shortName = shortName;
        driver.category = category;
        driver.nationality = nationality;
        return driver;
    }

}
