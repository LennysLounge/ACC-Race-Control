/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import java.util.logging.Logger;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnection.State.OFFLINE;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsDisconnetedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class ConfigurationController
    implements EventListener{

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(ConfigurationController.class.getName());

    private final ConfigurationPanel panel;

    private final GoogleSheetsAPIExtension sheetsAPI;

    public ConfigurationController() {
        EventBus.register(this);
        panel = new ConfigurationPanel();
        panel.connectButton.setAction(() -> connectButton());
        sheetsAPI = GoogleSheetsAPIExtension.getInstance();
    }
    
    @Override
    public void onEvent(Event e) {
        if(e instanceof GoogleSheetsDisconnetedEvent){
            RaceControlApplet.runLater(() -> {
                panel.allowInput = true;
                panel.updateComponents();
                panel.invalidate();
            });
        }
    }

    private void connectButton() {
        if (sheetsAPI.getState() == OFFLINE) {
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

    public LPContainer getPanel() {
        return panel;
    }

    

}
