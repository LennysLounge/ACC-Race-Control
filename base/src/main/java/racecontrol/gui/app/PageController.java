/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface PageController {

    /**
     * Returns a panel for this controller.
     *
     * @return A panel for this controller.
     */
    public LPContainer getPanel();

    /**
     * Returns the menu item for this controller.
     *
     * @return The menu item for this controller.
     */
    public MenuItem getMenuItem();

}
