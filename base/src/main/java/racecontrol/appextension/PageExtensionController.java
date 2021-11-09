/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.appextension;

import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface PageExtensionController {

    /**
     * Returns a panel for this controller.
     *
     * @return A panel for this controller.
     */
    public LPContainer getPanel();

}
