/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.results;

import racecontrol.eventbus.Event;
import racecontrol.client.extension.contact.ContactEvent;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.client.protocol.SessionId;
import racecontrol.client.protocol.BroadcastingEvent;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.protocol.enums.BroadcastingEventType;
import racecontrol.client.protocol.enums.SessionPhase;
import racecontrol.client.protocol.enums.SessionType;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.ClientExtension;

/**
 *
 * @author Leonard
 */
public class ResultsExtension
        extends ClientExtension {

    private static final Logger LOG = Logger.getLogger(ResultsExtension.class.getName());

    private String currentFilePath = null;

    private List<BroadcastingEvent> broadcastingEvents = new LinkedList<>();
    private List<ContactEvent> incidents = new LinkedList<>();

    private boolean isMeasuringGreenFlagOffset = false;
    private long greenFlagOffsetTimestamp = 0;
    private long greenFlagOffset = 0;

    private final IncidentReport report = new IncidentReport();

    public ResultsExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChangedEvent) {
            createNewFile(((SessionChangedEvent) e).getSessionId());
            broadcastingEvents.clear();
            incidents.clear();
            report.incidents.clear();
            report.broadcastEvents.clear();
            report.greenFlagOffset = 0;

        } else if (e instanceof BroadcastingEventEvent) {
            if (((BroadcastingEventEvent) e).getEvent().getType() == BroadcastingEventType.ACCIDENT) {
                broadcastingEvents.add(((BroadcastingEventEvent) e).getEvent());
                report.broadcastEvents.add(((BroadcastingEventEvent) e).getEvent());
                writeStateToFile();
            }
        } else if (e instanceof ContactEvent) {
            incidents.add((ContactEvent) e);
            report.incidents.add(((ContactEvent) e).getInfo());
            writeStateToFile();
        } else if (e instanceof SessionPhaseChangedEvent) {
            SessionInfo info = ((SessionPhaseChangedEvent) e).getSessionInfo();
            if (info.getSessionType() == SessionType.RACE) {
                if (info.getPhase() == SessionPhase.STARTING) {
                    isMeasuringGreenFlagOffset = true;
                    greenFlagOffsetTimestamp = System.currentTimeMillis();
                } else if (info.getPhase() == SessionPhase.SESSION && isMeasuringGreenFlagOffset) {
                    greenFlagOffset = System.currentTimeMillis() - greenFlagOffsetTimestamp;
                    report.greenFlagOffset = greenFlagOffset;
                    isMeasuringGreenFlagOffset = false;
                }
            }
        }
    }

    private void createNewFile(SessionId id) {
        LOG.info("Session Changed, creating dirs and files");

        File directory = new File("results");
        directory.mkdir();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        currentFilePath = "results/"
                + dateFormat.format(new Date())
                + "_"
                + id.getType().name()
                + ".json";
    }

    private void writeStateToFile() {
        try {
            String result = new ObjectMapper().writeValueAsString(report);
            FileWriter writer = new FileWriter(currentFilePath);
            writer.write(result);
            writer.close();

        } catch (JsonProcessingException ex) {
            Logger.getLogger(ResultsExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultsExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error writing results to file: {}.\ncurrentFilePath:" + currentFilePath, e);
        }
    }

}
