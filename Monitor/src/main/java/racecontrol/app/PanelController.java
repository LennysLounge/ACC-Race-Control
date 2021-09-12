/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import racecontrol.lpgui.gui.LPComponent;

/**
 * A PanelController controlls a ui panel.
 *
 * @author Leonard
 */
public interface PanelController {

    /**
     * Returns the panel that is controlled.
     *
     * @return the panel that is controlled.
     */
    public LPComponent getPanel();
}
