/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.googlesheetsapi;

import java.util.logging.Logger;
import racecontrol.app.PanelController;
import racecontrol.googlesheetsapi.GoogleSheetsAPIController;
import racecontrol.googlesheetsapi.GoogleSheetsConfiguration;
import racecontrol.lpgui.gui.LPComponent;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIConfigurationController
        implements PanelController {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIConfigurationController.class.getName());

    private final GoogleSheetsAPIConfigurationPanel panel;

    private final GoogleSheetsAPIController sheetsAPI;

    public GoogleSheetsAPIConfigurationController() {
        panel = new GoogleSheetsAPIConfigurationPanel();
        panel.connectButton.setAction(() -> connectButton());
        sheetsAPI = GoogleSheetsAPIController.getInstance();
    }

    @Override
    public LPComponent getPanel() {
        return panel;
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
            panel.allowInput = false;
        } else {
            //disable spreadsheet service.
            sheetsAPI.stop();
            panel.allowInput = true;
        }
        panel.updateComponents();
        panel.invalidate();
    }

}
