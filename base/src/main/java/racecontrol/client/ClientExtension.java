/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client;

import racecontrol.client.model.Model;

/**
 * Base class for an extension to the client.
 * @author Leonard
 */
public abstract class ClientExtension {

    /**
     * Reference to the writable data model.
     */
    private Model writabelModel = new Model();

    protected Model getWritableModel() {
        return writabelModel;
    }

    protected void setWritableModel(Model model) {
        this.writabelModel = model;
    }

}
