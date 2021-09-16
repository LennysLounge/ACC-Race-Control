/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.googlesheetsapi;

import java.util.logging.Logger;
import racecontrol.googlesheetsapi.GoogleSheetsAPIController;
import racecontrol.googlesheetsapi.GoogleSheetsConfiguration;
import racecontrol.lpgui.gui.LPComponent;
import racecontrol.lpgui.gui.LPTabPanel;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIConfigurationController {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIConfigurationController.class.getName());
    
    private final LPTabPanel tabPanel = new LPTabPanel();

    private final GoogleSheetsAPIConfigurationPanel panel;

    private final GoogleSheetsAPIController sheetsAPI;

    public GoogleSheetsAPIConfigurationController() {
        panel = new GoogleSheetsAPIConfigurationPanel();
        panel.connectButton.setAction(() -> connectButton());
        sheetsAPI = GoogleSheetsAPIController.getInstance();
        
        tabPanel.addTab(panel);
        tabPanel.setSize(660, 460);
        tabPanel.setName("Google Sheets API");
    }

    public LPComponent getPanel() {
        return tabPanel;
    }

    private void connectButton() {
        if (!sheetsAPI.isRunning()) {
            //enable spreadsheet service.
            sheetsAPI.start(new GoogleSheetsConfiguration(
                    panel.spreadSheetLinkTextField.getValue(),
                    panel.credentialsFileTextField.getValue(),
                    panel.findRowRangeTextField.getValue(),
                    panel.replayOffsetTextField.getValue(),
                    panel.sessionColumnTextField.getValue(),
                    panel.carColumnTextField.getValue()
            ));
            tabPanel.addTab(sheetsAPI.getPanel());
            panel.allowInput = false;
        } else {
            //disable spreadsheet service.
            sheetsAPI.stop();
            tabPanel.removeTab(sheetsAPI.getPanel());
            panel.allowInput = true;
        }
        panel.updateComponents();
        panel.invalidate();
    }

}
