/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsTargetChangedEvent
        extends Event {

    private final String target;

    public GoogleSheetsTargetChangedEvent(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

}
