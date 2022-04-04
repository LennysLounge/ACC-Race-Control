/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.racereport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import racecontrol.client.extension.raceevent.entries.RaceEventEntry;
import racecontrol.client.protocol.SessionId;
import racecontrol.client.protocol.enums.SessionType;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.extension.laptimes.LapCompletedEvent;
import racecontrol.client.extension.results.ResultsExtension;
import racecontrol.utility.TimeUtils;
import racecontrol.client.ClientExtension;
import racecontrol.client.extension.raceevent.RaceEventExtension;

/**
 *
 * @author Leonard
 */
public class RaceReportExtension extends ClientExtension
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static RaceReportExtension instance;
    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(RaceReportExtension.class.getName());
    /**
     * maps sessionId to a session. Session maps a carId to a driver Record.
     */
    private final Map<SessionId, Map<Integer, DriverRecord>> sessions = new HashMap<>();
    /**
     * Leader offset maps a lap number to a timestamp when the leader completed
     * that lap.
     */
    private final Map<Integer, Long> leaderOffset = new HashMap<>();
    /**
     * current session id.
     */
    private SessionId sessionId;

    public static RaceReportExtension get() {
        if (instance == null) {
            instance = new RaceReportExtension();
        }
        return instance;
    }

    private RaceReportExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof LapCompletedEvent) {
            onLapCompleted((LapCompletedEvent) e);
        } else if (e instanceof SessionChangedEvent) {
            sessionId = ((SessionChangedEvent) e).getSessionId();
            sessions.put(sessionId, new HashMap<>());
            leaderOffset.clear();
        }
    }

    private void onLapCompleted(LapCompletedEvent e) {
        // add a driver record if one does not exist.
        if (!sessions.get(sessionId).containsKey(e.getCar().id)) {
            String driverName = e.getCar().drivers.stream()
                    .map(driverInfo -> driverInfo.getFirstName() + " " + driverInfo.getLastName())
                    .collect(Collectors.joining(", "));
            sessions.get(sessionId).put(e.getCar().id,
                    new DriverRecord(
                            driverName,
                            String.valueOf(e.getCar().carNumber)
                    )
            );
        }
        DriverRecord dr = sessions.get(sessionId).get(e.getCar().id);

        //set position and lap count
        dr.setPosition(e.getCar().position);
        dr.setLapCount(e.getCar().lapCount);

        int deltaToLeader = 0;

        // if we are in a race session, calculate offset to leader.
        if (sessionId.getType() == RACE) {
            //if this car is the leader add the leader offset.
            int lapCount = e.getCar().lapCount;
            long now = System.currentTimeMillis();
            if (!leaderOffset.containsKey(lapCount)) {
                leaderOffset.put(lapCount, now);
            }

            deltaToLeader = (int) (now - leaderOffset.get(lapCount));
        }

        dr.getLaps().put(dr.getLapCount(), new LapRecord(e.getLapTime(), deltaToLeader));
        /*
        LOG.info("Lap recorded for #" + e.getCar().getCarNumber()
                + "\ttime: " + TimeUtils.asLapTime(e.getLapTime())
                + "\toffset: " + TimeUtils.asDelta(deltaToLeader));
         */
    }

    /**
     * Saves a race report to disk.
     */
    public void saveRaceReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export race report");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("Json file (.json)", ".json");
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("Comma seperated value (.csv)", ".csv");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Human readable (.txt)", ".txt");
        //fileChooser.setFileFilter(jsonFilter);
        //fileChooser.setFileFilter(csvFilter);
        fileChooser.setFileFilter(txtFilter);

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // translate event list to event recrods.
            List<EventRecord> records = new ArrayList<>();
            for (RaceEventEntry entry : RaceEventExtension.get().getEntries()) {
                records.add(new EventRecord(entry.getSessionTime(),
                        entry.getTypeDescriptor(),
                        entry.getInfo(),
                        entry.getReplayTime(),
                        entry.getSessionId()));
            }

            //Save file
            LOG.info("Saving event list to " + fileChooser.getSelectedFile().getAbsolutePath());
            if (fileChooser.getFileFilter() == jsonFilter) {
                saveReportAsJson(fileChooser.getSelectedFile(), records);
            } else if (fileChooser.getFileFilter() == csvFilter) {
                saveReportAsCSV(fileChooser.getSelectedFile(), records);
            } else if (fileChooser.getFileFilter() == txtFilter) {
                saveReportAsTXT(fileChooser.getSelectedFile(), records);
            }
        }
    }

    private void saveReportAsJson(File f, List<EventRecord> records) {
        File file = new File(f.getAbsoluteFile() + ".json");
        LOG.info("saving as json to " + file.getAbsolutePath());
        try ( FileWriter writer = new FileWriter(file)) {
            String result = new ObjectMapper().writeValueAsString(records);
            writer.write(result);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ResultsExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultsExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error writing results to file: {}.\ncurrentFilePath:" + file.getAbsolutePath(), e);
        }
    }

    private void saveReportAsCSV(File f, List<EventRecord> records) {
        File file = new File(f.getAbsoluteFile() + ".csv");
        LOG.info("saving as csv to " + file.getAbsolutePath());
        try ( FileWriter writer = new FileWriter(file)) {
            for (EventRecord record : records) {
                writer.write(TimeUtils.asDuration(record.getSessionTime()));
                writer.write(",");
                writer.write(record.getTypeDesciptor());
                writer.write(",");
                writer.write(record.getInfo());
                writer.write(",");
                writer.write(TimeUtils.asDuration(record.getReplayTime()));
                writer.write("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(ResultsExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error writing results to file: {}.\ncurrentFilePath:" + file.getAbsolutePath(), e);
        }
    }

    private void saveReportAsTXT(File f, List<EventRecord> records) {
        f.mkdirs();
        Map<SessionType, Integer> typeCount = new HashMap<>();
        typeCount.put(SessionType.PRACTICE, 1);
        typeCount.put(SessionType.QUALIFYING, 1);
        typeCount.put(SessionType.RACE, 1);
        for (SessionId sId : sessions.keySet()) {
            String fileName = sId.getType().name()
                    + typeCount.get(sId.getType())
                    + ".txt";
            typeCount.put(sId.getType(), typeCount.get(sId.getType()) + 1);

            File outfile = new File(f.getAbsolutePath() + "/" + fileName);
            saveReportAsTXT_2(outfile, records, sId);
        }
    }

    private void saveReportAsTXT_2(File outfile, List<EventRecord> records, SessionId sId) {
        try ( FileWriter writer = new FileWriter(outfile)) {
            // write entry list
            writer.write("- Entry List -\n");
            for (DriverRecord dr : sessions.get(sId).values()) {
                writer.write("#" + dr.getCarNumber()
                        + "\t" + dr.getDriverName() + "\n");
            }

            // write positions and laps
            writer.write("\n- Session Results -\n");
            // sort records by their finishing delta and their lap count.
            List<DriverRecord> sortedRecords = sessions.get(sId).values().stream()
                    .sorted((DriverRecord dr1, DriverRecord dr2) -> {
                        if (dr1.getLapCount() == dr2.getLapCount()) {
                            return (int) Math.signum(dr1.getLaps().get(dr1.getLapCount()).getDeltaToLeader()
                                    - dr2.getLaps().get(dr2.getLapCount()).getDeltaToLeader());
                        } else {
                            return -(dr1.getLapCount() - dr2.getLapCount());
                        }
                    })
                    .collect(Collectors.toList());

            for (int i = 0; i < sortedRecords.size(); i++) {
                DriverRecord dr = sortedRecords.get(i);
                writer.write(String.format("P%2d  ", i));
                writer.write(String.format("%4s", "#" + dr.getCarNumber()));
                writer.write(String.format("\tLaps: %3d", dr.getLapCount()));
                writer.write("\tLaps: ()");
                writer.write("\n");
            }

            // write race events
            writer.write("\n- Session Events -\n");
            for (EventRecord r : records) {
                if (r.getSessionId() == sId) {
                    writer.write("Time: " + TimeUtils.asDuration(r.getSessionTime()));
                    writer.write(String.format(" %-30s", r.getTypeDesciptor()));
                    writer.write(String.format(" %-20s", r.getInfo()));
                    writer.write("Replay: " + TimeUtils.asDuration(r.getReplayTime()));
                    writer.write("\n");
                }
            }

            if (sId.getType() == RACE) {
                // write lap charts.
                writer.write("\n- Lap Chart -\n");
                //find lowest lap to start chart at.
                int startLap = 10000000;
                int endLap = 0;

                for (DriverRecord dr : sortedRecords) {
                    for (Integer lapNr : dr.getLaps().keySet()) {
                        if (lapNr < startLap) {
                            startLap = lapNr;
                        }
                    }
                    if (dr.getLapCount() > endLap) {
                        endLap = dr.getLapCount();
                    }
                }

                for (int i = startLap; i <= endLap; i++) {
                    // sort records for each lap
                    sortedRecords = sortDriverRecordsForLap(sortedRecords, i);
                    writer.write("\nLap " + i + "\n");
                    for (DriverRecord dr : sortedRecords) {
                        writer.write("#" + dr.getCarNumber() + "\t");
                        // write delta to leader
                        if (dr.getLaps().containsKey(i)) {
                            writer.write(String.format("%10s",
                                    TimeUtils.asDelta(dr.getLaps().get(i).getDeltaToLeader())));
                        } else {
                            // if driver has not completed this lap
                            // write delta from last lap that was completed
                            writer.write(String.format("(Lap %d) %10s",
                                    dr.getLapCount(),
                                    TimeUtils.asDelta(dr.getLaps().get(dr.getLapCount()).getDeltaToLeader()))
                            );
                        }
                        writer.write("\n");
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ResultsExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error writing results to file: {}.\ncurrentFilePath:" + outfile.getAbsolutePath(), e);
        }
    }

    private List<DriverRecord> sortDriverRecordsForLap(List<DriverRecord> records, int lap) {
        List<DriverRecord> result = new ArrayList<>();
        int lapCount = lap;
        while (lapCount > 0 && result.size() != records.size()) {
            //sort records for lap
            final int constLapCount = lapCount;
            records.stream()
                    .filter(dr -> dr.getLapCount() >= constLapCount)
                    .filter(dr -> !result.contains(dr))
                    .sorted((dr1, dr2) -> compareDriverRecordAtLap(dr1, dr2, constLapCount))
                    .forEach(dr -> result.add(dr));
            lapCount--;
        }
        return result;
    }

    private int compareDriverRecordAtLap(DriverRecord dr1, DriverRecord dr2, int lap) {
        if (dr1.getLaps().containsKey(lap) && dr2.getLaps().containsKey(lap)) {
            return (int) Math.signum(dr1.getLaps().get(lap).getDeltaToLeader()
                    - dr2.getLaps().get(lap).getDeltaToLeader());
        }
        if (dr1.getLaps().containsKey(lap)) {
            return 1;
        }
        if (dr2.getLaps().containsKey(lap)) {
            return -1;
        }
        return 0;
    }
}
