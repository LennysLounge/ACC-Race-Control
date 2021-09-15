/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.RaceControlApplet;
import racecontrol.app.AppController;
import racecontrol.app.racecontrol.entries.ContactEventEntry;
import racecontrol.app.racecontrol.entries.RaceEventEntry;
import racecontrol.app.racecontrol.entries.SimpleEventEntry;
import racecontrol.app.racecontrol.googlesheetsapi.GoogleSheetsAPIConfigurationController;
import racecontrol.client.AccBroadcastingClient;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.extension.contact.ContactInfo;
import racecontrol.client.extension.contact.ContactEvent;
import racecontrol.client.extension.racereport.RaceReportController;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.extension.replayoffset.ReplayStartKnownEvent;
import racecontrol.client.extension.replayoffset.ReplayStartRequiresSearchEvent;

/**
 *
 * @author Leonard
 */
public class RaceControlController
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static RaceControlController instance;

    private static final Logger LOG = Logger.getLogger(RaceControlController.class.getName());

    private AccBroadcastingClient client;

    private RaceControlPanel panel;

    private RaceEventTableModel tableModel;

    private ReplayOffsetExtension replayOffsetExtension;

    private RaceReportController raceReportController;

    private AppController appController;

    private GoogleSheetsAPIConfigurationController googleSheetsConfigController;
    /**
     * Indicates that the google sheets config window is open.
     */
    private boolean googleSheetsConfigClosed = true;

    public static RaceControlController getInstance() {
        if (instance == null) {
            instance = new RaceControlController();
        }
        return instance;
    }

    private RaceControlController() {
    }

    public void initialise() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        panel = new RaceControlPanel();
        tableModel = new RaceEventTableModel();
        replayOffsetExtension = ReplayOffsetExtension.getInstance();
        appController = AppController.getInstance();
        googleSheetsConfigController = new GoogleSheetsAPIConfigurationController();
        raceReportController = RaceReportController.getInstance();

        tableModel.setInfoColumnAction(infoClickAction);
        tableModel.setReplayClickAction((RaceEventEntry entry, int mouseX, int mouseY) -> replayClickAction(entry));

        panel.getTable().setTableModel(tableModel);
        panel.setKeyEvent(() -> {
            createDummyContactEvent();
        });

        panel.getSeachReplayButton().setAction(() -> {
            replayOffsetExtension.findSessionChange();
            panel.getSeachReplayButton().setEnabled(false);
        });

        panel.googleSheetsButton.setAction(() -> openGoogleSheetsConfig());
        panel.exportButton.setAction(() -> exportEventList());
    }

    public RaceControlPanel getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ReplayStartRequiresSearchEvent) {
            RaceControlApplet.runLater(() -> {
                panel.setShowReplayButton(true);
            });
        } else if (e instanceof ReplayStartKnownEvent) {
            RaceControlApplet.runLater(() -> {
                panel.setShowReplayButton(false);
            });
            updateReplayTimes();
        } else if (e instanceof SessionChangedEvent) {
            addSessionChangeEntry((SessionChangedEvent) e);
            tableModel.setSessionId(((SessionChangedEvent) e).getSessionId());
        } else if (e instanceof ContactEvent) {
            addContactEntry((ContactEvent) e);
        }
    }

    private final RaceEventTableModel.ClickAction infoClickAction
            = (RaceEventEntry entry, int mouseX, int mouseY) -> {
                if (entry instanceof ContactEventEntry) {
                    ((ContactEventEntry) entry).onInfoClicked(mouseX, mouseY);
                }
            };

    private void replayClickAction(RaceEventEntry entry) {
        if (entry.isHasReplay() && entry.getReplayTime() != -1) {
            LOG.info("Starting instant replay for incident");
            client.sendInstantReplayRequestWithCamera(
                    entry.getSessionTime() - 10000,
                    10,
                    getClient().getModel().getSessionInfo().getFocusedCarIndex(),
                    getClient().getModel().getSessionInfo().getActiveCameraSet(),
                    getClient().getModel().getSessionInfo().getActiveCamera()
            );
        }
    }

    private void addSessionChangeEntry(SessionChangedEvent event) {
        SessionInfo info = event.getSessionInfo();
        String text = "";
        switch (info.getSessionType()) {
            case PRACTICE:
                text = "Practice session";
                break;
            case QUALIFYING:
                text = "Qualifying session";
                break;
            case RACE:
                text = "Race session";
                break;
        }
        text += event.isInitialisation() ? " joined." : " started";
        tableModel.addEntry(new SimpleEventEntry(event.getSessionId(), info.getSessionTime(), text, false));
        panel.getTable().invalidate();
    }

    private void addContactEntry(ContactEvent event) {
        ContactInfo info = event.getInfo();
        tableModel.addEntry(new ContactEventEntry(client.getSessionId(), info.getSessionEarliestTime(),
                "Contact", true, info));
        panel.getTable().invalidate();
    }

    private void openGoogleSheetsConfig() {
        if (googleSheetsConfigClosed) {
            appController.launchNewWindow(
                    googleSheetsConfigController,
                    false,
                    () -> {
                        googleSheetsConfigClosed = true;
                        panel.googleSheetsButton.setEnabled(true);
                    });
            googleSheetsConfigClosed = false;
            panel.googleSheetsButton.setEnabled(false);
        }
    }

    private void exportEventList() {
        raceReportController.saveRaceReport();
    }

    private void createDummyContactEvent() {
        int nCars = (int) Math.floor(Math.random() * Math.min(6, client.getModel().getCarsInfo().size()) + 1);
        float sessionTime = client.getModel().getSessionInfo().getSessionTime();
        ContactInfo incident = new ContactInfo(
                sessionTime,
                0,
                client.getSessionId());
        for (int i = 0; i < nCars; i++) {
            incident = incident.addCar(
                    sessionTime,
                    getRandomCar(),
                    0);
        }
        ContactEvent event = new ContactEvent(incident);
        addContactEntry(event);
    }

    private CarInfo getRandomCar() {
        int r = (int) Math.floor(Math.random() * client.getModel().getCarsInfo().size());
        int i = 0;
        for (CarInfo car : getClient().getModel().getCarsInfo().values()) {
            if (i++ == r) {
                return car;
            }
        }
        return null;
    }

    private void updateReplayTimes() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            RaceEventEntry entry = tableModel.getEntry(i);
            if (entry.isHasReplay() && entry.getReplayTime() == -1) {
                entry.setReplayTime(replayOffsetExtension.getReplayTimeFromSessionTime((int) entry.getSessionTime()));
            }
        }
    }

    public List<RaceEventEntry> getRaceEvents() {
        List<RaceEventEntry> entries = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            entries.add(tableModel.getEntry(i));
        }
        return entries;
    }

}
