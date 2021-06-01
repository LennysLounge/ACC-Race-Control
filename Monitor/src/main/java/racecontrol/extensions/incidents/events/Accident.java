/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents.events;

import racecontrol.eventbus.Event;
import racecontrol.extensions.incidents.IncidentInfo;

/**
 *
 * @author Leonard
 */
public class Accident extends Event {

    private IncidentInfo info;

    public Accident(IncidentInfo info) {
        this.info = info;
    }

    public IncidentInfo getInfo() {
        return info;
    }

}
