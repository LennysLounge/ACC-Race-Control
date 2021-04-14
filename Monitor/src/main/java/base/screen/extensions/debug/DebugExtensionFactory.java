/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.debug;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class DebugExtensionFactory
        implements ACCLiveTimingExtensionFactory {

    private final DebugConfigPanel configPanel;
    private DebugExtension extension;

    public DebugExtensionFactory() {
        configPanel = new DebugConfigPanel();
    }

    @Override
    public String getName() {
        return "Debug";
    }

    @Override
    public void createExtension() {
        removeExtension();
        if (configPanel.isExtensionEnabled()) {
            extension = new DebugExtension();
        }
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return configPanel;
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
