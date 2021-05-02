/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.laptimes;

import base.screen.extensions.AccClientExtension;
import base.ACCLiveTimingExtensionFactory;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class LaptimeExtensionFactory
        implements ACCLiveTimingExtensionFactory {

    private AccClientExtension extension;

    @Override
    public String getName() {
        return "Laptime extension";
    }

    @Override
    public void createExtension() {
        removeExtension();
        extension = new LapTimeExtension(false);
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }

    @Override
    public void removeExtension() {
        if (extension != null) {
            extension.removeExtension();
            extension = null;
        }
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

}
