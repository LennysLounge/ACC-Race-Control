/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol;

import java.util.logging.Logger;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.gui.RaceControlApplet;
import racecontrol.client.extension.raceevent.entries.ContactEventEntry;
import racecontrol.client.extension.raceevent.entries.RaceEventEntry;
import racecontrol.client.extension.raceevent.entries.VSCViolationEventEntry;
import racecontrol.gui.app.racecontrol.virtualsafetycar.VirtualSafetyCarConfigController;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.extension.contact.ContactInfo;
import racecontrol.client.extension.contact.ContactEvent;
import racecontrol.client.extension.contact.ContactExtensionEnabledEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnectedEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsDisconnetedEvent;
import racecontrol.client.extension.raceevent.RaceEventEvent;
import racecontrol.client.extension.raceevent.entries.ReturnToGarageEntry;
import racecontrol.client.extension.racereport.RaceReportExtension;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.extension.replayoffset.ReplayStartKnownEvent;
import racecontrol.client.extension.replayoffset.ReplayStartRequiresSearchEvent;
import racecontrol.client.extension.returntogarage.ReturnToGarageEnabledEvent;
import racecontrol.client.extension.returntogarage.ReturnToGarageExtension;
import racecontrol.client.extension.vsc.events.VSCEndEvent;
import racecontrol.client.extension.vsc.events.VSCStartEvent;
import racecontrol.client.model.Car;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_GRAY;
import static racecontrol.gui.RaceControlApplet.getApplet;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.gui.app.racecontrol.contact.ContactConfigController;
import racecontrol.gui.app.racecontrol.googlesheetsapi.GoogleSheetsController;
import racecontrol.logging.UILogger;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.CONTACT_CONFIG_ENABLED;

/**
 *
 * @author Leonard
 */
public class RaceControlController
        implements EventListener, PageController {

    private static final Logger LOG = Logger.getLogger(RaceControlController.class.getName());

    private final RaceControlPanel panel = new RaceControlPanel();

    private final RaceEventTableModel tableModel = new RaceEventTableModel();

    private final GoogleSheetsController googleSheetsController = new GoogleSheetsController();

    private final VirtualSafetyCarConfigController virtualSafetyCarController = new VirtualSafetyCarConfigController();

    private final ContactConfigController contactConfigController = new ContactConfigController();

    private final Menu.MenuItem menuItem;

    public RaceControlController() {
        menuItem = new MenuItem("Race Control",
                getApplet().loadResourceAsPImage("/images/RC_Menu_Control.png"));
        EventBus.register(this);

        tableModel.setInfoColumnAction(infoClickAction);
        tableModel.setReplayClickAction((RaceEventEntry entry, int mouseX, int mouseY) -> replayClickAction(entry));

        panel.getTable().setTableModel(tableModel);
        panel.setKeyEvent(() -> {
            createDummyContactEvent();
        });

        panel.getSeachReplayButton().setAction(() -> {
            ReplayOffsetExtension.getInstance().findSessionChange();
            panel.getSeachReplayButton().setEnabled(false);
        });

        panel.exportButton.setAction(RaceReportExtension.get()::saveRaceReport);
        panel.googleSheetsButton.setAction(googleSheetsController::openSettingsPanel);
        panel.virtualSafetyCarButton.setAction(virtualSafetyCarController::openSettingsPanel);
        panel.contactButton.setAction(contactConfigController::openSettingsPanel);
        panel.returnToGarageButton.setAction(this::returnToGarageButtonPressed);

        updateContactButton(PersistantConfig.get(CONTACT_CONFIG_ENABLED));
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
        } else if (e instanceof SessionChangedEvent) {
            RaceControlApplet.runLater(() -> {
                tableModel.setSessionId(((SessionChangedEvent) e).getSessionId());
            });
        } else if (e instanceof RaceEventEvent) {
            tableModel.addEntry(((RaceEventEvent) e).getEntry());
        } else if (e instanceof ContactExtensionEnabledEvent) {
            RaceControlApplet.runLater(() -> {
                updateContactButton(
                        ((ContactExtensionEnabledEvent) e).getNewState()
                );
            });
        } else if (e instanceof ReturnToGarageEnabledEvent) {
            RaceControlApplet.runLater(()
                    -> updateRTGButton(
                            ((ReturnToGarageEnabledEvent) e).getNewState()
                    )
            );
        } else if (e instanceof VSCStartEvent) {
            RaceControlApplet.runLater(() -> updateVSCButton(true));
        } else if (e instanceof VSCEndEvent) {
            RaceControlApplet.runLater(() -> updateVSCButton(false));
        } else if (e instanceof GoogleSheetsConnectedEvent) {
            RaceControlApplet.runLater(() -> updateGoogleSheetsButton(true));
        } else if (e instanceof GoogleSheetsDisconnetedEvent) {
            RaceControlApplet.runLater(() -> updateGoogleSheetsButton(false));
        }
    }

    private void updateContactButton(boolean isHighlighted) {
        if (isHighlighted) {
            panel.contactButton.setBackgroundColor(COLOR_DARK_RED);
        } else {
            panel.contactButton.setBackgroundColor(COLOR_GRAY);
        }
    }

    private void updateVSCButton(boolean isHighlighted) {
        if (isHighlighted) {
            panel.virtualSafetyCarButton.setBackgroundColor(COLOR_DARK_RED);
        } else {
            panel.virtualSafetyCarButton.setBackgroundColor(COLOR_GRAY);
        }
    }

    private void updateGoogleSheetsButton(boolean isHighlighted) {
        if (isHighlighted) {
            panel.googleSheetsButton.setBackgroundColor(COLOR_DARK_RED);
        } else {
            panel.googleSheetsButton.setBackgroundColor(COLOR_GRAY);
        }
    }

    private void updateRTGButton(boolean isHighlighted) {
        if (isHighlighted) {
            panel.returnToGarageButton.setBackgroundColor(COLOR_DARK_RED);
        } else {
            panel.returnToGarageButton.setBackgroundColor(COLOR_GRAY);
        }
    }

    private void returnToGarageButtonPressed() {
        ReturnToGarageExtension.getInstance().setEnabled(
                !ReturnToGarageExtension.getInstance().isEnabled()
        );
    }

    private final RaceEventTableModel.ClickAction infoClickAction
            = (RaceEventEntry entry, int mouseX, int mouseY) -> {
                if (entry instanceof ContactEventEntry) {
                    ((ContactEventEntry) entry).onInfoClicked(mouseX, mouseY);
                } else if (entry instanceof VSCViolationEventEntry) {
                    ((VSCViolationEventEntry) entry).onInfoClicked(mouseX, mouseY);
                } else if (entry instanceof ReturnToGarageEntry) {
                    ((ReturnToGarageEntry) entry).onInfoClicked(mouseX, mouseY);
                }
            };

    private void replayClickAction(RaceEventEntry entry) {
        if (entry.isHasReplay() && entry.getReplayTime() != -1) {
            LOG.info("Starting instant replay for incident");
            getClient().sendInstantReplayRequestWithCamera(
                    entry.getSessionTime() - 5000,
                    10,
                    getClient().getModel().session.raw.getFocusedCarIndex(),
                    getClient().getModel().session.raw.getActiveCameraSet(),
                    getClient().getModel().session.raw.getActiveCamera()
            );
        }
    }

    private void createDummyContactEvent() {
        int nCars = (int) Math.floor(Math.random() * Math.min(6, getClient().getModel().cars.size()) + 1);
        int sessionTime = getClient().getModel().session.raw.getSessionTime();
        ContactInfo incident = new ContactInfo(
                sessionTime,
                0,
                getClient().getModel().currentSessionId);
        for (int i = 0; i < nCars; i++) {
            incident = incident.withCar(
                    sessionTime,
                    getRandomCar());
        }
        UILogger.log("contact blar blar blar");
        ContactEvent event = new ContactEvent(incident);

        ContactInfo info = event.getInfo();
        tableModel.addEntry(new ContactEventEntry(getClient().getModel().currentSessionId, info.getSessionEarliestTime(),
                info.isGameContact() ? "Contact" : "Possible contact",
                true, info));
        panel.getTable().invalidate();
    }

    private Car getRandomCar() {
        int r = (int) Math.floor(Math.random() * getClient().getModel().cars.size());
        int i = 0;
        for (Car car : getClient().getModel().cars.values()) {
            if (i++ == r) {
                return car;
            }
        }
        return null;
    }
}
