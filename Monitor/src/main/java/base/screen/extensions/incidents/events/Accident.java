/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.incidents.events;

import base.screen.eventbus.Event;
import base.screen.extensions.incidents.IncidentInfo;

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
