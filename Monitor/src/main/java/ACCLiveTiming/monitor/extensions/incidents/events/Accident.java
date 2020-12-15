/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.extensions.incidents.events;

import acclivetiming.monitor.eventbus.Event;
import acclivetiming.monitor.extensions.incidents.IncidentInfo;

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
