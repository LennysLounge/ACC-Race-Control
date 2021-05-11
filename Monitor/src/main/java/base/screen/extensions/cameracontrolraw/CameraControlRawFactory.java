/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.cameracontrolraw;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class CameraControlRawFactory
        implements ACCLiveTimingExtensionFactory {

    CameraControlRawExtension extension;

    @Override
    public String getName() {
        return "Camera Controls";
    }

    @Override
    public void createExtension() {
        removeExtension();
        if (true) {
            extension = new CameraControlRawExtension();
        }

    }

    @Override
    public void removeExtension() {
        if (extension != null) {
            extension.removeExtension();
            extension = null;
        }
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
