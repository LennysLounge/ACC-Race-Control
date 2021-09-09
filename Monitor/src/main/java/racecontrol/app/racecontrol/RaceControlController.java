/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import java.util.logging.Logger;
import racecontrol.app.racecontrol.entries.ContactEventEntry;
import racecontrol.app.racecontrol.entries.RaceEventEntry;
import racecontrol.app.racecontrol.entries.SimpleEventEntry;
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

/**
 *
 * @author Leonard
 */
public class RaceControlController
        implements EventListener {

    private static final Logger LOG = Logger.getLogger(RaceControlController.class.getName());

    private final RaceControlPanel panel;

    private final RaceEventTableModel tableModel;

    public RaceControlController() {
        EventBus.register(this);
        panel = new RaceControlPanel();
        tableModel = new RaceEventTableModel();

        tableModel.setInfoColumnAction(infoClickAction);
        tableModel.setReplayClickAction((RaceEventEntry entry, int mouseX, int mouseY) -> {
            LOG.info("Replay area clicked");
        });

        panel.getTable().setTableModel(tableModel);

        panel.setKeyEvent(() -> {
            createDummyContactEvent();
        });

    }

    public RaceControlPanel getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChangedEvent) {
            addSessionChangeEntry((SessionChangedEvent) e);
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
        tableModel.addEntry(new SimpleEventEntry(info.getSessionTime(), text, false));
        panel.getTable().invalidate();
    }

    private void addContactEntry(ContactEvent event) {
        ContactInfo info = event.getInfo();
        tableModel.addEntry(new ContactEventEntry(info.getSessionEarliestTime(),
                "Contact", true, info));
        panel.getTable().invalidate();
    }

    private void createDummyContactEvent() {
        AccBroadcastingClient client = AccBroadcastingClient.getClient();
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
        AccBroadcastingClient client = AccBroadcastingClient.getClient();
        int r = (int) Math.floor(Math.random() * client.getModel().getCarsInfo().size());
        int i = 0;
        for (CarInfo car : getClient().getModel().getCarsInfo().values()) {
            if (i++ == r) {
                return car;
            }
        }
        return null;
    }

}
