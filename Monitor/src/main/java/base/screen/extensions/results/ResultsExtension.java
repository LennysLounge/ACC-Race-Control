/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.results;

import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.incidents.events.Accident;
import base.screen.networking.BroadcastingEventEvent;
import base.screen.networking.SessionChanged;
import base.screen.networking.SessionId;
import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.enums.BroadcastingEventType;
import base.screen.networking.enums.SessionPhase;
import base.screen.networking.enums.SessionType;
import base.screen.networking.events.SessionPhaseChanged;
import base.screen.visualisation.gui.LPContainer;
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

/**
 *
 * @author Leonard
 */
public class ResultsExtension
        implements EventListener, AccClientExtension {

    private static final Logger LOG = Logger.getLogger(ResultsExtension.class.getName());

    private String currentFilePath = null;

    private List<BroadcastingEvent> broadcastingEvents = new LinkedList<>();
    private List<Accident> incidents = new LinkedList<>();

    private boolean isMeasuringGreenFlagOffset = false;
    private long greenFlagOffsetTimestamp = 0;
    private long greenFlagOffset = 0;

    private IncidentReport report = new IncidentReport();

    public ResultsExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            createNewFile(((SessionChanged) e).getSessionId());
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
        } else if (e instanceof Accident) {
            incidents.add((Accident) e);
            report.incidents.add(((Accident) e).getInfo());
            writeStateToFile();
        } else if (e instanceof SessionPhaseChanged) {
            SessionInfo info = ((SessionPhaseChanged) e).getSessionInfo();
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

    @Override
    public LPContainer getPanel() {
        return null;
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
        } catch(Exception e){
            LOG.log(Level.SEVERE, "Error writing results to file: {}.\ncurrentFilePath:" + currentFilePath, e);
        }

        /*
            try {
            FileWriter writer = new FileWriter(currentFilePath);

            writer.write("{\n");
            writer.write("\t\"approximateReplayOffset\":" + greenFlagOffset + ",\n");
            writer.write("\t\"raw\": [\n");
            for (BroadcastingEvent event : broadcastingEvents) {
            writer.write("\t\t{\n");
            writer.write("\t\t\t\"sessionTimeMS\": " + event.getTimeMs() + ",\n");
            writer.write("\t\t\t\"carId\": " + event.getCarId() + ",\n");
            writer.write("\t\t\t\"message\": \"" + event.getMessage() + "\"\n");
            writer.write("\t\t},\n");
            }
            writer.write("\t],\n");
            writer.write("\t\"incidents\": [\n");
            for (Accident accident : incidents) {
            writer.write("\t\t{\n");
            IncidentInfo info = accident.getInfo();
            
            writer.write("\t\t\t\"sessionTimeMS\":" + ((int) accident.getInfo().getEarliestTime()) + ",\n");
            writer.write("\t\t\t\"carsInvolved\": [\n");
            for (CarInfo car : info.getCars()) {
            writer.write("\t\t\t\t{\n");
            writer.write("\t\t\t\t\t\"carId\": " + car.getCarId() + ",\n");
            writer.write("\t\t\t\t\t\"carNumber\": " + car.getCarNumber() + ",\n");
            writer.write("\t\t\t\t\t\"driver\": \""
            + car.getDriver().getFirstName() + " " + car.getDriver().getLastName()
            + "\",\n");
            writer.write("\t\t\t\t\t\"lap\": " + car.getRealtime().getLaps() + "\n");
            writer.write("\t\t\t\t},\n");
            }
            writer.write("\t\t\t]\n");
            
            writer.write("\t\t},\n");
            }
            writer.write("\t],\n");
            writer.write("}\t");

            writer.close();
            
            } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            }
         */
    }

    private String getCarNumberAndLapCount(CarInfo car) {
        return car.getCarNumber() + " [" + (car.getRealtime().getLaps() + 1) + "]";
    }

    @Override
    public void removeExtension() {
        EventBus.unregister(this);
    }

}
