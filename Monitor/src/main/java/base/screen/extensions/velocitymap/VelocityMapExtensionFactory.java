/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.velocitymap;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
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
        extension = new VelocityMapExtension();
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
