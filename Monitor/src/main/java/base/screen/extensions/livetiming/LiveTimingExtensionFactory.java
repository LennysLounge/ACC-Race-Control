/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.screen.extensions.AccClientExtension;
import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.GeneralExtentionConfigPanel;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtensionFactory
        implements ACCLiveTimingExtensionFactory {

    private AccClientExtension extension;

    @Override
    public String getName() {
        return "Live Timing Extension";
    }

    @Override
    public void createExtension() {
        removeExtension();
        if (GeneralExtentionConfigPanel.getInstance().isLiveTimingEnabled()) {
            extension = new LiveTimingExtension();
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
