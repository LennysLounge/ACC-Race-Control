/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.laptimes;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.client.extension.AccBroadcastingClientExtensionModule;

/**
 *
 * @author Leonard
 */
public class LaptimeExtensionModule
        implements AccBroadcastingClientExtensionModule {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public AccClientExtension createExtension(AccBroadcastingClient client) {
        return new LapTimeExtension(client, false);
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }

    @Override
    public Class getExtensionClass() {
        return LapTimeExtension.class;
    }
}
