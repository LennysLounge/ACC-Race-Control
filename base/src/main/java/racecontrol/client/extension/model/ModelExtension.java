/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import racecontrol.client.ClientExtension;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventListener;

/**
 * Updates the data model.
 *
 * @author Leonard
 */
public class ModelExtension extends ClientExtension
        implements EventListener {

    @Override
    public void onEvent(Event e) {
    }

}
