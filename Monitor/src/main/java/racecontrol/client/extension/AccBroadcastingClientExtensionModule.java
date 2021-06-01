/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface AccBroadcastingClientExtensionModule {

    /**
     * Returns true if the extension is enabled.
     *
     * @return true if the extension is enabled.
     */
    public boolean isEnabled();

    /**
     * Creates the client extension.
     *
     * @param client Reference to the client.
     * @return the created extension.
     */
    public AccClientExtension createExtension(AccBroadcastingClient client);

    /**
     * Gives the configuration dialog panel for this extension. Returns null if
     * this extension does not have a configuration panel.
     *
     * @return
     */
    public LPContainer getExtensionConfigurationPanel();

    /**
     * Returns the class of the extentsion that this module creates.
     *
     * @return The class of the extension.
     */
    public Class getExtensionClass();
}
