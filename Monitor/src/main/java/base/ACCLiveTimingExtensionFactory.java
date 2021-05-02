/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package base;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface ACCLiveTimingExtensionFactory {

    /**
     * Returns the name for this extension.
     *
     * @return The name.
     */
    public String getName();

    /**
     * Creates the client extension.
     *
     */
    public void createExtension();

    /**
     * Removes the extension.
     */
    public void removeExtension();

    /**
     * Gives the configuration dialog panel for this extension. Returns null if
     * this extension does not have a configuration panel.
     *
     * @return
     */
    public LPContainer getExtensionConfigurationPanel();

    /**
     * returns the extension.
     *
     * @return
     */
    public AccClientExtension getExtension();
}
