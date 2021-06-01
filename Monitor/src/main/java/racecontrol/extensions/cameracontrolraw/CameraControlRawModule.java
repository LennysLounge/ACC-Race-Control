/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.cameracontrolraw;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.extensions.GeneralExtentionConfigPanel;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.client.extension.AccBroadcastingClientExtensionModule;

/**
 *
 * @author Leonard
 */
public class CameraControlRawModule
        implements AccBroadcastingClientExtensionModule {

    @Override
    public boolean isEnabled() {
        return GeneralExtentionConfigPanel.getInstance().isCameraControlsEnabled();
    }

    @Override
    public AccClientExtension createExtension(AccBroadcastingClient client) {
        return new CameraControlRawExtension(client);
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }

    @Override
    public Class getExtensionClass() {
        return CameraControlRawExtension.class;
    }
}
