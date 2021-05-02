/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.logging;

import base.screen.extensions.AccClientExtension;
import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.GeneralExtentionConfigPanel;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class LoggingExtensionFactory
        implements ACCLiveTimingExtensionFactory {

    private AccClientExtension extension;

    @Override
    public String getName() {
        return "Logging extension";
    }

    @Override
    public void createExtension() {
        removeExtension();
        if (GeneralExtentionConfigPanel.getInstance().isLoggingEnabled()) {
            extension = new LoggingExtension();
        }
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
