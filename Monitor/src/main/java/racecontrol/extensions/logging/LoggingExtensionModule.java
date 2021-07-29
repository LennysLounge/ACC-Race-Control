/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.logging;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.visualisation.components.GeneralExtentionConfigPanel;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.client.extension.AccBroadcastingClientExtensionModule;

/**
 *
 * @author Leonard
 */
public class LoggingExtensionModule
        implements AccBroadcastingClientExtensionModule {

    @Override
    public boolean isEnabled() {
        return GeneralExtentionConfigPanel.getInstance().isLoggingEnabled();
    }

    @Override
    public AccClientExtension createExtension(AccBroadcastingClient client) {
        return new LoggingExtension(client);
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }
    
    @Override
    public Class getExtensionClass() {
        return LoggingExtension.class;
    }
}
