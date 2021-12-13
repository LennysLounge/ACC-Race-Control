/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnectedEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsDisconnetedEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsTargetChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class ControlController
        implements EventListener {

    /**
     * Reference to the extension.
     */
    private final GoogleSheetsAPIExtension GOOGLE_SHEETS;

    private final ControlPanel panel;

    public ControlController() {
        EventBus.register(this);
        GOOGLE_SHEETS = GoogleSheetsAPIExtension.getInstance();
        panel = new ControlPanel();

        panel.sendTestIncidentButton.setAction(() -> {
            GOOGLE_SHEETS.sendIncident(0, "test");
        });
    }

    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof GoogleSheetsConnectedEvent) {
            RaceControlApplet.runLater(() -> {
                addTargetButtons((GoogleSheetsConnectedEvent) e);
                updateTargetLabel();
            });
        } else if (e instanceof GoogleSheetsDisconnetedEvent) {
            RaceControlApplet.runLater(() -> {
                panel.removeTargetButtons();
            });
        } else if (e instanceof GoogleSheetsTargetChangedEvent) {
            RaceControlApplet.runLater(() -> {
                updateTargetLabel();
            });
        }
    }

    private void updateTargetLabel() {
        panel.targetLabel.setText(GOOGLE_SHEETS.getSpreadsheetTitle()
                + "::" + GOOGLE_SHEETS.getSheetTarget());
        panel.invalidate();
    }

    private void addTargetButtons(GoogleSheetsConnectedEvent event) {

        for (String sheetTarget : GOOGLE_SHEETS.getValidSheets()) {
            LPButton b = new LPButton("target " + sheetTarget);
            b.setAction(() -> {
                GOOGLE_SHEETS.setSheetTarget(sheetTarget);
            });
            panel.addTargetButton(b);
        }

        panel.invalidate();
    }

}
