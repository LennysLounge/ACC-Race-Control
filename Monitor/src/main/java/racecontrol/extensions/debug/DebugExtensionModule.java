/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.debug;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.client.extension.AccBroadcastingClientExtensionModule;

/**
 *
 * @author Leonard
 */
public class DebugExtensionModule
        implements AccBroadcastingClientExtensionModule {

    private final DebugConfigPanel configPanel;

    public DebugExtensionModule() {
        configPanel = new DebugConfigPanel();
    }

    @Override
    public boolean isEnabled() {
        return configPanel.isExtensionEnabled();
    }

    @Override
    public AccClientExtension createExtension(AccBroadcastingClient client) {
        return new DebugExtension(client);
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return configPanel;
    }

    @Override
    public Class getExtensionClass() {
        return DebugExtension.class;
    }

}
