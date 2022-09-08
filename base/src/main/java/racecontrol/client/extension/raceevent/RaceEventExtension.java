/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.raceevent;

import java.util.ArrayList;
import java.util.List;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.ClientExtension;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.extension.contact.ContactEvent;
import racecontrol.client.extension.contact.ContactInfo;
import racecontrol.client.extension.raceevent.entries.ContactEventEntry;
import racecontrol.client.extension.raceevent.entries.RaceEventEntry;
import racecontrol.client.extension.raceevent.entries.SimpleEventEntry;
import racecontrol.client.extension.raceevent.entries.VSCViolationEventEntry;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.extension.replayoffset.ReplayStartKnownEvent;
import racecontrol.client.extension.vsc.events.VSCEndEvent;
import racecontrol.client.extension.vsc.events.VSCStartEvent;
import racecontrol.client.extension.vsc.events.VSCViolationEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;

/**
 *
 * @author Leonard
 */
public class RaceEventExtension
        extends ClientExtension {

    /**
     * Singleton instance.
     */
    private static RaceEventExtension instance;
    /**
     * List of all race events.
     */
    private final List<RaceEventEntry> entries = new ArrayList<>();

    public static RaceEventExtension get() {
        if (instance == null) {
            instance = new RaceEventExtension();
        }
        return instance;
    }

    private RaceEventExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChangedEvent) {
            addSessionChangeEntry((SessionChangedEvent) e);
        } else if (e instanceof ContactEvent) {
            addContactEntry((ContactEvent) e);
        } else if (e instanceof VSCStartEvent) {
            onVSCStart((VSCStartEvent) e);
        } else if (e instanceof VSCEndEvent) {
            onVSCEnd((VSCEndEvent) e);
        } else if (e instanceof VSCViolationEvent) {
            onVSCViolation((VSCViolationEvent) e);
        } else if (e instanceof ReplayStartKnownEvent) {
            updateReplayTimes();
        }
    }

    public List<RaceEventEntry> getEntries() {
        return entries;
    }

    private void updateReplayTimes() {
        for (var entry : entries) {
            if (entry.isHasReplay() && entry.getReplayTime() == -1) {
                entry.setReplayTime(ReplayOffsetExtension.getInstance()
                        .getReplayTimeFromSessionTime((int) entry.getSessionTime()));
            }
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
        RaceEventEntry entry = new SimpleEventEntry(event.getSessionId(),
                info.getSessionTime(), text, false);
        entries.add(entry);
        EventBus.publish(new RaceEventEvent(entry));
    }

    private void addContactEntry(ContactEvent event) {
        ContactInfo info = event.getInfo();
        RaceEventEntry entry = new ContactEventEntry(getClient().getModel().currentSessionId,
                info.getSessionEarliestTime(),
                info.isGameContact() ? "Contact" : "Possible contact",
                true,
                info);
        entries.add(entry);
        EventBus.publish(new RaceEventEvent(entry));
    }

    private void onVSCStart(VSCStartEvent e) {
        String descriptor = "Virtual safety car started.   Speedlimit: "
                + e.getSpeedLimit() + " kmh";
        RaceEventEntry entry = new SimpleEventEntry(e.getSessionId(),
                e.getTimeStamp(),
                descriptor,
                false);
        entries.add(entry);
        EventBus.publish(new RaceEventEvent(entry));
    }

    private void onVSCEnd(VSCEndEvent e) {
        RaceEventEntry entry = new SimpleEventEntry(e.getSessionId(),
                e.getSessionTime(),
                "Virtual safety car ending",
                false);
        entries.add(entry);
        EventBus.publish(new RaceEventEvent(entry));
    }

    private void onVSCViolation(VSCViolationEvent e) {
        RaceEventEntry entry = new VSCViolationEventEntry(e.getSessionId(),
                e.getSessionTime(),
                "VSC violation",
                false,
                e);
        entries.add(entry);
        EventBus.publish(new RaceEventEvent(entry));
    }

}
