/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import java.util.stream.Collectors;
import racecontrol.app.racecontrol.entries.SimpleEventEntry;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.SessionChanged;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.extensions.incidents.IncidentInfo;
import racecontrol.extensions.incidents.events.Accident;

/**
 *
 * @author Leonard
 */
public class RaceControlController
        implements EventListener {

    private final RaceControlPanel panel;

    private final RaceEventTableModel tableModel;

    public RaceControlController() {
        EventBus.register(this);
        panel = new RaceControlPanel();
        tableModel = new RaceEventTableModel();

        panel.getTable().setTableModel(tableModel);

    }

    public RaceControlPanel getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            addSessionChangeEntry((SessionChanged) e);
        } else if (e instanceof Accident) {
            addContactEntry((Accident) e);
        }
    }

    private void addSessionChangeEntry(SessionChanged event) {
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
        tableModel.addEntry(new SimpleEventEntry(info.getSessionTime(),
                text, false, 0));
        panel.getTable().invalidate();
    }

    private void addContactEntry(Accident event) {
        IncidentInfo info = event.getInfo();
        String text = "Contact with " + info.getCars().stream()
                .map(i -> ("#" + i.getCarNumber()))
                .collect(Collectors.joining(", "));
        tableModel.addEntry(new SimpleEventEntry(info.getSessionEarliestTime(),
                text, true, info.getReplayTime()));
        panel.getTable().invalidate();
    }

}
