/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.debug;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 * A Basic Extension to test stuff out with.
 *
 * @author Leonard
 */
public class DebugExtension implements AccClientExtension {

    DebugPanel panel;

    public DebugExtension() {
        this.panel = new DebugPanel();
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void removeExtension() {
    }

}
