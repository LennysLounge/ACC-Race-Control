/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.velocitymap;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.GeneralExtentionConfigPanel;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class VelocityMapExtensionFactory
        implements ACCLiveTimingExtensionFactory {

    VelocityMapExtension extension;

    @Override
    public String getName() {
        return "Velocity Map";
    }

    @Override
    public void createExtension() {
        removeExtension();
        if (GeneralExtentionConfigPanel.getInstance().isVelocityMapEnabled()) {
            extension = new VelocityMapExtension();
        }
    }

    @Override
    public void removeExtension() {
        if (extension != null) {
            extension.removeExtension();
        }
        extension = null;
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

}
