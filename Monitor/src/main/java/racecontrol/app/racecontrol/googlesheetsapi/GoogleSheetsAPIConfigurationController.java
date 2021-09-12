/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.googlesheetsapi;

import racecontrol.app.PanelController;
import racecontrol.lpgui.gui.LPComponent;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIConfigurationController 
    implements PanelController{
    
    private final GoogleSheetsAPIConfigurationPanel panel;
    
    public GoogleSheetsAPIConfigurationController(){
        panel = new GoogleSheetsAPIConfigurationPanel();
    }

    @Override
    public LPComponent getPanel() {
        return panel;
    }
    
}
