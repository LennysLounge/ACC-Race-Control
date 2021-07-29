/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.broadcasting;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.extension.AccBroadcastingClientExtensionModule;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.visualisation.components.GeneralExtentionConfigPanel;
import racecontrol.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class BroadcastingModule
        implements AccBroadcastingClientExtensionModule {

    @Override
    public boolean isEnabled() {
        return GeneralExtentionConfigPanel.getInstance().isBroadcastingEnabled();
    }

    @Override
    public AccClientExtension createExtension(AccBroadcastingClient client) {
        return new BroadcastingExtension(client);
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }

    @Override
    public Class getExtensionClass() {
        return BroadcastingExtension.class;
    }

}
