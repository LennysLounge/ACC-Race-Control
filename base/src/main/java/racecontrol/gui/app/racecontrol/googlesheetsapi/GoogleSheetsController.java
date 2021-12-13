/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnectedEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsDisconnetedEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsError;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.AppController;
import racecontrol.gui.lpui.LPTabPanel;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsController
        implements EventListener {

    /**
     * Reference to the app controller.
     */
    private final AppController appController;

    private final LPTabPanel tabPanel = new LPTabPanel();

    private final ConfigurationController configController;

    private final ControlController controlController;

    public GoogleSheetsController() {
        EventBus.register(this);
        appController = AppController.getInstance();
        configController = new ConfigurationController();
        controlController = new ControlController();

        tabPanel.addTab(configController.getPanel());
        tabPanel.setSize(660, 460);
        tabPanel.setName("Google Sheets API");
    }

    public void openSettingsPanel() {
        appController.launchNewWindow(tabPanel, false);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof GoogleSheetsConnectedEvent) {
            RaceControlApplet.runLater(() -> {
                tabPanel.addTab(controlController.getPanel());
            });
        } else if (e instanceof GoogleSheetsDisconnetedEvent) {
            RaceControlApplet.runLater(() -> {
                tabPanel.removeTab(controlController.getPanel());
                tabPanel.invalidate();
                var event = (GoogleSheetsDisconnetedEvent) e;
                if (event.hasError()) {
                    handleExit(event.getErrorInfo());
                }
            });
        }
    }

    private void handleExit(GoogleSheetsError error) {
        switch (error.getErrorCode()) {
            default:
                JOptionPane.showMessageDialog(null, "There was an error in the google sheets api.\n"
                        + error.getException().getMessage(),
                        "Google sheets error.", ERROR_MESSAGE);
                return;
            case 400:
                JOptionPane.showMessageDialog(null, error.getReason(),
                        "Google sheets error: Bad request", ERROR_MESSAGE);
                return;
            case 403:
                JOptionPane.showMessageDialog(null, "This account does not have permission to edit this document.",
                        "Google sheets error: Permission denied", ERROR_MESSAGE);
                return;
        }
    }

}
