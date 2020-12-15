/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.extensions.googlesheetsapi;

import acclivetiming.monitor.extensions.AccClientExtension;
import acclivetiming.monitor.visualisation.gui.LPContainer;
import javax.swing.JPanel;
import acclivetiming.ACCLiveTimingExtensionModule;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtensionModule implements ACCLiveTimingExtensionModule{
    
    private GoogleSheetsAPIExtension extension;
    private GoogleSheetsAPIPanel panel;
    private GoogleSheetsAPIConfigurationPanel configurationPanel;
    
    public GoogleSheetsAPIExtensionModule(){
        configurationPanel = new GoogleSheetsAPIConfigurationPanel();
        extension = new GoogleSheetsAPIExtension();
        panel = new GoogleSheetsAPIPanel(extension);
    }

    @Override
    public String getName() {
        return "Google Sheets API extension";
    }

    @Override
    public AccClientExtension getExtension() {
        if(configurationPanel.isExtensionEnabled()){
            extension.start(configurationPanel.getSpreadSheetLink());
            return extension;
        }
        return null;
    }

    @Override
    public LPContainer getExtensionPanel() {
        if(configurationPanel.isExtensionEnabled()){
            return panel;
        }
        return null;
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return configurationPanel;
    }
    
}
