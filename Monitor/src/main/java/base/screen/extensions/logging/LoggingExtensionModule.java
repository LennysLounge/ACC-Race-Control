/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.logging;

import base.ACCLiveTimingExtensionModule;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class LoggingExtensionModule
        implements ACCLiveTimingExtensionModule {

    private LoggingExtension extension;
    private LoggingPanel panel;

    public LoggingExtensionModule() {
        extension = new LoggingExtension();
        panel = new LoggingPanel(extension);

    }

    @Override
    public String getName() {
        return "Logging extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

    @Override
    public LPContainer getExtensionPanel() {
        return panel;
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }

}
