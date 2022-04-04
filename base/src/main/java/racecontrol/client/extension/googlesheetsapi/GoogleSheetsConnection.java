/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import racecontrol.Main;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnection.State.CONNECTING;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnection.State.OFFLINE;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnection.State.RUNNING;
import racecontrol.client.model.Car;
import racecontrol.eventbus.EventBus;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsConnection {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsConnection.class.getName());
    /**
     * The configuration to connect to a spreadsheet.
     */
    private GoogleSheetsConfiguration configuration;
    /**
     * Event queue.
     */
    private final LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    /**
     * The Sheet service for the spreadsheet api.
     */
    private GoogleSheetsService sheetService;
    /**
     * Current state of the connection.
     */
    private State state = OFFLINE;
    /**
     * Event loop.
     */
    private Thread eventLoop;
    /**
     * Current target sheet.
     */
    private String sheetTarget = "";
    /**
     * Info about the connected spreadsheet.
     */
    private Spreadsheet spreadsheet;

    public State getState() {
        return state;
    }

    public String getSheetTarget() {
        return sheetTarget;
    }

    public void setSheetTarget(String sheetName) {
        sheetTarget = sheetName + "!";
        EventBus.publish(new GoogleSheetsTargetChangedEvent(sheetName));
    }

    public String getSpreadSheetTitle() {
        if (spreadsheet != null) {
            return spreadsheet.getProperties().getTitle();
        }
        return "";
    }

    public void start(GoogleSheetsConfiguration configuration) {
        this.configuration = configuration;

        try {
            sheetService = new GoogleSheetsService(configuration.getSpreadsheetLink(), configuration.getCredentialsPath());
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "The spreadsheet URL is not valid.", ex);
            JOptionPane.showMessageDialog(null, "The given spreadsheet link is not valid."
                    + "\nMake sure you copy the whole URL.",
                    "Error extracting spreadsheet Id", ERROR_MESSAGE);
            return;
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error starting the Google Sheets service.", e);
            JOptionPane.showMessageDialog(null, "There was an error starting the Google API service.",
                    "Error starting API Service", ERROR_MESSAGE);
            return;
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Cannot load credentials file: ", ex);
            JOptionPane.showMessageDialog(null, "There was an error loading the Google API credentials."
                    + "\nThe file could not be found.",
                    "Error loading API credentials", ERROR_MESSAGE);
            return;
        }

        //Start event loop
        LOG.info("Starting Google sheets service event loop");
        state = CONNECTING;
        eventLoop = new Thread("google sheets") {
            @Override
            public void run() {
                Thread.setDefaultUncaughtExceptionHandler(new Main.UncoughtExceptionHandler());
                try {
                    eventLoop();
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "Sheet service event loop interrupted.", e);
                }
                LOG.info("Google sheets event loop done.");
            }
        };
        eventLoop.start();
    }

    public void stop() {
        if (state != OFFLINE) {
            LOG.info("Stopping google sheets service");
            queue.add(new QuitEvent());
        }
    }

    public void sendIncident(String time, String info) {
        if (state == RUNNING || state == CONNECTING) {
            queue.add(new SendIncidentEvent(time, info));
        }
    }

    public void sendGreenFlagOffset(int time) {
        if (state == RUNNING || state == CONNECTING) {
            queue.add(new GreenFlagEvent(time));
        }
    }

    public void sendCarConnection(Car car) {
        if (state == RUNNING || state == CONNECTING) {
            sendCarsConnection(Arrays.asList(car));
        }
    }

    public void sendCarsConnection(List<Car> cars) {
        if (state == RUNNING || state == CONNECTING) {
            queue.add(new SendCarConnectedEvent(cars));
        }
    }

    private void eventLoop() throws InterruptedException {
        GoogleSheetsError error = null;
        try {
            getSpreadsheet();

            state = RUNNING;
            EventBus.publish(new GoogleSheetsConnectedEvent(spreadsheet));

            while (state == RUNNING) {
                Object o = queue.take();
                if (o instanceof QuitEvent) {
                    state = OFFLINE;
                    queue.clear();
                }
                if (o instanceof SendIncidentEvent) {
                    sendIncident((SendIncidentEvent) o);
                }
                if (o instanceof GreenFlagEvent) {
                    sendGreenFlag((GreenFlagEvent) o);
                }
                if (o instanceof SendCarConnectedEvent) {
                    sendCarEntries((SendCarConnectedEvent) o);
                }
            }
        } catch (IOException e) {
            error = handleException(e);
            state = OFFLINE;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error in event loop. Aborting.", e);
            error = new GoogleSheetsError(e, -1, "Error in google sheets api.");
            state = OFFLINE;
        }

        EventBus.publish(new GoogleSheetsDisconnetedEvent(error));
    }

    private GoogleSheetsError handleException(IOException exception) {
        LOG.log(Level.SEVERE, exception.getMessage(), exception);
        if (exception instanceof GoogleJsonResponseException) {
            var e = (GoogleJsonResponseException) exception;
            String reason = e.getDetails().getErrors().get(0).getMessage();
            return new GoogleSheetsError(e, e.getStatusCode(), reason);
        }
        return new GoogleSheetsError(exception, -1, "Error in google sheets api.");
    }

    private void sendIncident(SendIncidentEvent event) throws IOException {

        String sheet = sheetTarget;
        String rangeSession = sheet + configuration.getFindEmptyRowRange();

        List<List<Object>> values;

        values = sheetService.getCells(rangeSession);

        int emptyLine = values.size() + 1;

        rangeSession = sheet + configuration.getSessionTimeColumn() + emptyLine;
        String rangeCars = sheet + configuration.getCarInfoColumn() + emptyLine;

        List<List<Object>> lineSession = new LinkedList<>();
        lineSession.add(Arrays.asList(event.sessionTime));
        List<List<Object>> lineCars = new LinkedList<>();
        lineCars.add(Arrays.asList(event.carsInvolved));

        sheetService.updateCells(rangeSession, lineSession);
        sheetService.updateCells(rangeCars, lineCars);
    }

    private void sendCarEntries(SendCarConnectedEvent event) throws IOException {
        String sheet = "'entry list'!";
        String range = sheet + "B3:C";

        // get values from spreadsheet and translate to entries.
        Map<String, Integer> entries = new HashMap<>();
        for (List<Object> row : sheetService.getCells(range)) {
            if (!row.isEmpty()) {
                String name = String.valueOf(row.get(0));
                int number;
                try {
                    number = Integer.parseInt(String.valueOf(row.get(1)));
                } catch (NumberFormatException e) {
                    number = -1;
                }
                entries.put(name, number);
            }
        }

        // add new cars and update car number if they changed.
        event.carsConnected.stream()
                .forEach(car -> {
                    String name = car.drivers.stream()
                            .map(driver -> driver.fullName())
                            .collect(Collectors.joining("\n"));
                    entries.put(name, car.carNumber);
                });

        // sort based on car number and translate convert to row column lists.
        List<List<Object>> values = entries.entrySet().stream()
                .sorted((e1, e2) -> e1.getValue() - e2.getValue())
                .map(entry -> Arrays.asList((Object) entry.getKey(), (Object) entry.getValue()))
                .collect(Collectors.toList());

        sheetService.updateCells(range, values);
    }

    private void sendGreenFlag(GreenFlagEvent event) throws IOException {
        String sheet = sheetTarget;
        String range = sheet + configuration.getReplayOffsetCell();

        sheetService.updateCells(range, Arrays.asList(Arrays.asList(TimeUtils.asDuration(event.time))));
    }

    private void getSpreadsheet() throws IOException {
        spreadsheet = sheetService.getSpreadsheet();
    }

    public static class SendIncidentEvent {

        public String sessionTime;
        public String carsInvolved;

        public SendIncidentEvent(String sessionTime, String carsInvolved) {
            this.sessionTime = sessionTime;
            this.carsInvolved = carsInvolved;
        }
    }

    public static class SendCarConnectedEvent {

        public final List<Car> carsConnected;

        public SendCarConnectedEvent(List<Car> carsConnected) {
            this.carsConnected = new LinkedList<>(carsConnected);
        }
    }

    public static class QuitEvent {
    }

    public static class GreenFlagEvent {

        public int time;

        public GreenFlagEvent(int time) {
            this.time = time;
        }
    }

    public static enum State {
        RUNNING,
        CONNECTING,
        OFFLINE;
    }
}
