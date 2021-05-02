/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions;

import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface AccClientExtension {

    /**
     * Returns the attached panel for this extension.
     *
     * @return The attached panel.
     */
    public LPContainer getPanel();

    /**
     * Removes all necessary references related to this extension.
     */
    public void removeExtension();
}
