/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.appextension;

import racecontrol.client.ClientExtension;
import racecontrol.gui.app.PageController;

/**
 *
 * @author Leonard
 */
public interface AppExtensionModule {

    /**
     * Gives the name of this extension.
     *
     * @return The name of this extension.
     */
    public String getName();

    /**
     * Returns the client extension for this module.
     *
     * @return The client extension for this module.
     */
    public ClientExtension getExtension();

    /**
     * Returns the page controller for this module
     *
     * @return The page controller for this module.
     */
    public PageController getPageController();

}
