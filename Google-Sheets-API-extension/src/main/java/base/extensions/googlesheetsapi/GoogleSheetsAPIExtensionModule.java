/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.googlesheetsapi;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import javax.swing.JPanel;
import base.ACCLiveTimingExtensionFactory;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtensionModule implements ACCLiveTimingExtensionFactory{
    
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
    public AccClientExtension createExtension() {
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
