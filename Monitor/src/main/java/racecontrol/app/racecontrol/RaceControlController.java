/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.extension.replayoffset.ReplayStartKnownEvent;
import racecontrol.client.extension.replayoffset.ReplayStartRequiresSearchEvent;
import racecontrol.extensions.results.ResultsExtension;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class RaceControlController
        implements EventListener {

    private static final Logger LOG = Logger.getLogger(RaceControlController.class.getName());

    private final AccBroadcastingClient client;

    private final RaceControlPanel panel;

    private final RaceEventTableModel tableModel;

    private final ReplayOffsetExtension replayOffsetExtension;

    private final AppController appController;

    private final GoogleSheetsAPIConfigurationController googleSheetsConfigController;
    /**
     * Indicates that the google sheets config window is open.
     */
    private boolean googleSheetsConfigClosed = true;

    public RaceControlController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        panel = new RaceControlPanel();
        tableModel = new RaceEventTableModel();
        replayOffsetExtension = ReplayOffsetExtension.getInstance();
        appController = AppController.getInstance();
        googleSheetsConfigController = new GoogleSheetsAPIConfigurationController();

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
        tableModel.addEntry(new SimpleEventEntry(client.getSessionId(), info.getSessionTime(), text, false));
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export event list");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("Json file (.json)", ".json");
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("Comma seperated value (.csv)", ".csv");
        fileChooser.setFileFilter(jsonFilter);
        fileChooser.setFileFilter(csvFilter);

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // translate event list to event recrods.
            List<EventRecord> records = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                RaceEventEntry e = tableModel.getEntry(i);
                records.add(new EventRecord(e.getSessionTime(),
                        e.getTypeDescriptor(),
                        e.getInfo(),
                        e.getReplayTime()));
            }

            //Save file
            LOG.info("Saving event list to " + fileChooser.getSelectedFile().getAbsolutePath());
            if (fileChooser.getFileFilter() == jsonFilter) {
                saveEventListAsJson(fileChooser.getSelectedFile(), records);
            } else if (fileChooser.getFileFilter() == csvFilter) {
                saveEventListAsCSV(fileChooser.getSelectedFile(), records);
            }
        }
    }

    private void saveEventListAsJson(File f, List<EventRecord> records) {
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

    private void saveEventListAsCSV(File f, List<EventRecord> records) {
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

}
