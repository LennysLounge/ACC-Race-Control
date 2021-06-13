/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents;

/**
 * Represent an entry into the incident table model.
 *
 * @author Leonard
 */
public class IncidentEntry {

    private final IncidentInfo incident;
    private final int rows;
    private final Divider deviderType;
    
    public IncidentEntry(Divider deviderType){
        this.incident = null;
        this.rows = 1;
        this.deviderType = deviderType;
    }

    public IncidentEntry(IncidentInfo incident) {
        this(incident, Divider.NONE);
    }

    public IncidentEntry(IncidentInfo incident, Divider deviderType) {
        this.incident = incident;
        this.rows = (int) Math.ceil(incident.getCars().size() * 1f / IncidentTableModel.MAX_CARS_PER_ROW);
        this.deviderType = deviderType;
    }

    public IncidentInfo getIncident() {
        return incident;
    }

    public int getRows() {
        return rows;
    }

    public Divider getDividerType() {
        return deviderType;
    }

    public enum Divider {
        NONE,
        PRACTICE,
        QUALIFYING,
        RACE;
    }

}
