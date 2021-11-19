/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.racecontrol.entries.ContactEventEntry;
import racecontrol.gui.app.racecontrol.entries.RaceEventEntry;
import racecontrol.gui.app.racecontrol.entries.SimpleEventEntry;
import racecontrol.gui.app.racecontrol.entries.VSCViolationEventEntry;
import racecontrol.gui.app.racecontrol.googlesheetsapi.GoogleSheetsAPIConfigurationController;
import racecontrol.gui.app.racecontrol.virtualsafetycar.VirtualSafetyCarConfigController;
import racecontrol.gui.app.racecontrol.virtualsafetycar.controller.VSCEndEvent;
import racecontrol.gui.app.racecontrol.virtualsafetycar.controller.VSCStartEvent;
import racecontrol.gui.app.racecontrol.virtualsafetycar.controller.VSCViolationEvent;
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
import static racecontrol.gui.RaceControlApplet.getApplet;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.logging.UILogger;

/**
 *
 * @author Leonard
 */
public class RaceControlController
        implements EventListener, PageController {

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

    private GoogleSheetsAPIConfigurationController googleSheetsConfigController;

    private VirtualSafetyCarConfigController virtualSafetyCarController;

    private final Menu.MenuItem menuItem;

    public static RaceControlController getInstance() {
        if (instance == null) {
            instance = new RaceControlController();
            instance.initialise();
        }
        return instance;
    }

    private RaceControlController() {
        menuItem = new MenuItem("Race Control",
                getApplet().loadResourceAsPImage("/images/RC_Menu_Control.png"));
    }

    private void initialise() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        panel = new RaceControlPanel();
        tableModel = new RaceEventTableModel();
        replayOffsetExtension = ReplayOffsetExtension.getInstance();
        googleSheetsConfigController = new GoogleSheetsAPIConfigurationController();
        virtualSafetyCarController = new VirtualSafetyCarConfigController();
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

        panel.exportButton.setAction(() -> exportEventList());
        panel.googleSheetsButton.setAction(() -> googleSheetsConfigController.openSettingsPanel());
        panel.virtualSafetyCarButton.setAction(() -> virtualSafetyCarController.openSettingsPanel());
    }

    @Override
    public RaceControlPanel getPanel() {
        return panel;
    }

    @Override
    public Menu.MenuItem getMenuItem() {
        return menuItem;
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
        } else if (e instanceof VSCStartEvent) {
            onVSCStart((VSCStartEvent) e);
        } else if (e instanceof VSCEndEvent) {
            onVSCEnd((VSCEndEvent) e);
        } else if (e instanceof VSCViolationEvent) {
            onVSCViolation((VSCViolationEvent) e);
        }
    }

    private final RaceEventTableModel.ClickAction infoClickAction
            = (RaceEventEntry entry, int mouseX, int mouseY) -> {
                if (entry instanceof ContactEventEntry) {
                    ((ContactEventEntry) entry).onInfoClicked(mouseX, mouseY);
                } else if (entry instanceof VSCViolationEventEntry) {
                    ((VSCViolationEventEntry) entry).onInfoClicked(mouseX, mouseY);
                }
            };

    private void replayClickAction(RaceEventEntry entry) {
        if (entry.isHasReplay() && entry.getReplayTime() != -1) {
            LOG.info("Starting instant replay for incident");
            client.sendInstantReplayRequestWithCamera(
                    entry.getSessionTime() - 5000,
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

    private void onVSCStart(VSCStartEvent e) {
        String descriptor = "Virtual safety car started.   Speedlimit: "
                + e.getSpeedLimit() + " kmh";
        tableModel.addEntry(new SimpleEventEntry(e.getSessionId(),
                e.getTimeStamp(),
                descriptor,
                false));
        panel.getTable().invalidate();
    }

    private void onVSCEnd(VSCEndEvent e) {
        tableModel.addEntry(new SimpleEventEntry(e.getSessionId(),
                e.getSessionTime(),
                "Virtual safety car ending",
                false));
        panel.getTable().invalidate();
    }

    private void onVSCViolation(VSCViolationEvent e) {
        tableModel.addEntry(new VSCViolationEventEntry(e.getSessionId(),
                e.getSessionTime(),
                "VSC violation",
                false,
                e));
        panel.getTable().invalidate();
    }

    private void exportEventList() {
        raceReportController.saveRaceReport();
    }

    private void createDummyContactEvent() {
        int nCars = (int) Math.floor(Math.random() * Math.min(6, client.getModel().getCarsInfo().size()) + 1);
        int sessionTime = client.getModel().getSessionInfo().getSessionTime();
        ContactInfo incident = new ContactInfo(
                sessionTime,
                0,
                client.getSessionId());
        for (int i = 0; i < nCars; i++) {
            incident = incident.withCar(
                    sessionTime,
                    getRandomCar());
        }
        UILogger.log("contact blar blar blar");
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
