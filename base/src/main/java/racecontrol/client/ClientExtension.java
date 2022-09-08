/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client;

import racecontrol.client.model.Model;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * Base class for an extension to the client.
 *
 * @author Leonard
 */
public abstract class ClientExtension
        implements EventListener {

    /**
     * Reference to the writable data model.
     */
    private Model writabelModel = new Model();

    /**
     * A Client extension is registered to the event bus by default.
     */
    public ClientExtension() {
        EventBus.register(this);
    }

    /**
     * Returns the writable version of the system model.
     *
     * @return The writable version of the system model.
     */
    protected Model getWritableModel() {
        return writabelModel;
    }

    /**
     * Sets the writable version of the system model.
     *
     * @param model The writable version of the system model.
     */
    protected void setWritableModel(Model model) {
        this.writabelModel = model;
    }

}
