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
    private final String time;
    private final boolean inPits;
    private final DriverCategory category;

    public ListEntry(String position, String name, String carNumber, String time,
            boolean inPits, DriverCategory category) {
        this.position = position;
        this.name = name;
        this.carNumber = carNumber;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public boolean isInPits() {
        return inPits;
    }

    public DriverCategory getCategory() {
        return category;
    }
    

    
}
