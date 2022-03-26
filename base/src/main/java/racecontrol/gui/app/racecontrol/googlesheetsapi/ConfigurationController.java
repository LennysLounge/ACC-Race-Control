/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import java.util.logging.Logger;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnectedEvent;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnection.State.OFFLINE;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsDisconnetedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.AppController;
import racecontrol.gui.app.PanelWindowApplet;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class ConfigurationController
        implements EventListener {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(ConfigurationController.class.getName());

    /**
     * The configuration panel.
     */
    private final ConfigurationPanel panel;
    /**
     * Reference to the sheet api extension.
     */
    private final GoogleSheetsAPIExtension sheetsAPI;
    /**
     * Panel use to sign in with google
     */
    private final SignInWithGooglePanel googleSignInPanel = new SignInWithGooglePanel();
    /**
     * Window used to sign in with google.
     */
    private PanelWindowApplet googleSignInWindow = null;

    public ConfigurationController() {
        EventBus.register(this);
        panel = new ConfigurationPanel();
        panel.connectButton.setAction(this::connectButton);
        sheetsAPI = GoogleSheetsAPIExtension.getInstance();

        googleSignInPanel.signInButton.setAction(this::startSheetApi);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof GoogleSheetsConnectedEvent) {
            RaceControlApplet.runLater(() -> {
                panel.allowInput = false;
                panel.updateComponents();
                panel.invalidate();
            });
        } else if (e instanceof GoogleSheetsDisconnetedEvent) {
            System.out.println("disconnect");
            RaceControlApplet.runLater(() -> {
                panel.allowInput = true;
                panel.updateComponents();
                panel.invalidate();
            });
        }
    }

    private void startSheetApi() {
        if (sheetsAPI.getState() == OFFLINE) {
            //enable spreadsheet service.
            sheetsAPI.start(new GoogleSheetsConfiguration(
                    panel.spreadSheetLinkTextField.getValue(),
                    panel.getCredentialsPath(),
                    panel.findRowRangeTextField.getValue(),
                    panel.replayOffsetTextField.getValue(),
                    panel.sessionColumnTextField.getValue(),
                    panel.carColumnTextField.getValue()
            ));
        }
    }

    private void connectButton() {
        if (sheetsAPI.getState() == OFFLINE) {
            if (googleSignInWindow == null) {
                googleSignInWindow = RaceControlApplet
                        .launchNewWindow(googleSignInPanel, false);
                googleSignInWindow.addCloseAction(() -> {
                    googleSignInWindow = null;
                });
            } else {
                googleSignInWindow.grabFocus();
            }
        } else {
            //disable spreadsheet service.
            sheetsAPI.stop();
        }
    }

    public LPContainer getPanel() {
        return panel;
    }

}
