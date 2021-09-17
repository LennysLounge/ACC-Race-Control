/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.googlesheetsapi;

import java.io.FileNotFoundException;
import racecontrol.Main;
import racecontrol.client.data.SessionId;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.client.extension.contact.ContactInfo;
import racecontrol.client.extension.contact.ContactEvent;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.enums.SessionPhase;
import racecontrol.client.data.enums.SessionType;
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.utility.TimeUtils;
import racecontrol.lpgui.gui.LPContainer;
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
import racecontrol.app.racecontrol.virtualsafetycar.controller.VSCEndEvent;
import racecontrol.app.racecontrol.virtualsafetycar.controller.VSCStartEvent;
import racecontrol.app.racecontrol.virtualsafetycar.controller.VSCViolationEvent;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.logging.UILogger;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIController
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static GoogleSheetsAPIController instance;

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIController.class.getName());
    /**
     * Reference to the game connection client.
     */
    private final AccBroadcastingClient client;
    /**
     * The Sheet service for the spreadsheet api.
     */
    private GoogleSheetsService sheetService;
    /**
     * The configuration to connect to a spreadsheet.
     */
    private GoogleSheetsConfiguration configuration;
    /**
     * Indicates that the eventLoop is running.
     */
    private boolean running = false;
    /**
     * Event loop.
     */
    private Thread eventLoop;
    /**
     * Event queue.
     */
    private final LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    /**
     * The current sessionId.
     */
    private SessionId currentSessionId;
    /**
     * Current target sheet.
     */
    private String currentSheetTarget = "";

    private boolean isMeasuringGreenFlagOffset = false;
    private long greenFlagOffsetTimestamp = 0;

    private final GoogleSheetsAPIPanel panel;
    /**
     * List of recently connected cars to be send to the entry list.
     */
    private final List<CarInfo> carConnections = new LinkedList<>();

    public static GoogleSheetsAPIController getInstance() {
        if (instance == null) {
            instance = new GoogleSheetsAPIController();
        }
        return instance;
    }

    private GoogleSheetsAPIController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        panel = new GoogleSheetsAPIPanel(this);
    }

    @Override
    public void onEvent(Event e) {
        if (!running) {
            return;
        }
        if (e instanceof SessionChangedEvent) {
            onSessionChanged(((SessionChangedEvent) e).getSessionId());
            //start replay offset measuring
            if (!((SessionChangedEvent) e).isInitialisation()) {
                if (currentSessionId.getType() == SessionType.RACE) {
                    isMeasuringGreenFlagOffset = true;
                    greenFlagOffsetTimestamp = System.currentTimeMillis();
                }
            }
        } else if (e instanceof SessionPhaseChangedEvent) {
            SessionInfo info = ((SessionPhaseChangedEvent) e).getSessionInfo();
            if (info.getSessionType() == SessionType.RACE
                    && info.getPhase() == SessionPhase.SESSION
                    && isMeasuringGreenFlagOffset) {
                long diff = System.currentTimeMillis() - greenFlagOffsetTimestamp;
                queue.add(new GreenFlagEvent((int) diff));
                isMeasuringGreenFlagOffset = false;
            }
        } else if (e instanceof CarConnectedEvent) {
            // add car connection to a collection to send as a batch with
            // the next realtime update
            carConnections.add(((CarConnectedEvent) e).getCar());
        } else if (e instanceof RealtimeUpdateEvent) {
            // send car connections as a batch
            if (!carConnections.isEmpty()) {
                queue.add(new SendCarConnectedEvent(carConnections));
                carConnections.clear();
            }
        } else if (e instanceof ContactEvent) {
            ContactInfo info = ((ContactEvent) e).getInfo();
            String sessionTime = TimeUtils.asDuration(info.getSessionEarliestTime());
            String carNumbers = info.getCars().stream()
                    .map(car -> getCarNumberAndLapCount(car))
                    .collect(Collectors.joining("\n"));
            queue.add(new SendIncidentEvent(sessionTime, carNumbers));
            //LOG.info("accident received: " + carNumbers);
        } else if (e instanceof VSCStartEvent) {
            VSCStartEvent event = (VSCStartEvent) e;
            String text = String.format("VSC Start\n%d kmh", event.getSpeedLimit());
            String sessionTime = TimeUtils.asDuration(event.getTimeStamp());
            queue.add(new SendIncidentEvent(sessionTime, text));
            //LOG.info("VSC Start received: " + text);
        } else if (e instanceof VSCEndEvent) {
            VSCEndEvent event = (VSCEndEvent) e;
            String text = "VSC End";
            String sessionTime = TimeUtils.asDuration(event.getSessionTime());
            queue.add(new SendIncidentEvent(sessionTime, text));
            //LOG.info("VSC End received");
        } else if (e instanceof VSCViolationEvent) {
            VSCViolationEvent event = (VSCViolationEvent) e;
            CarInfo car = client.getModel().getCar(event.getCarId());
            String text = String.format("%s\n+%d kmh\n%s s",
                    car.getCarNumber(), event.getSpeedOver(),
                    TimeUtils.asDelta(event.getTimeOver()));
            String sessionTime = TimeUtils.asDuration(event.getSessionTime());
            queue.add(new SendIncidentEvent(sessionTime, text));
            //LOG.info("VSC violation received: " + text);
        }

        if (isGreenFlagOffsetBeeingMeasured()) {
            panel.invalidate();
        }
    }

    private void onSessionChanged(SessionId sessionId) {
        currentSessionId = sessionId;
        setCurrentTargetSheet(getTargetSheet(currentSessionId));
        LOG.info("Target Sheet changed to \"" + currentSheetTarget + "\"");
        UILogger.log("Spreasheet target changed to \"" + currentSheetTarget + "\"");
    }

    private String getCarNumberAndLapCount(CarInfo car) {
        return car.getCarNumber() + " [" + (car.getRealtime().getLaps() + 1) + "]";
    }

    private String getTargetSheet(SessionId sessionId) {
        switch (sessionId.getType()) {
            case PRACTICE:
                return "Practice!";
            case QUALIFYING:
                return "Qualifying!";
            case RACE:
                int raceNumber = Math.min(sessionId.getNumber() + 1, 2);
                return "Race " + String.valueOf(raceNumber) + "!";
            default:
                return "Practice!";
        }
    }

    public void sendEmptyIncident() {
        queue.add(new SendIncidentEvent(
                TimeUtils.asDuration(client.getModel().getSessionInfo().getSessionTime()),
                "empty")
        );
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
        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, "Error starting the Google Sheets service.", ex.getCause());
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
        running = true;
        eventLoop = new Thread("Sheet service event loop") {
            @Override
            public void run() {
                Thread.setDefaultUncaughtExceptionHandler(new Main.UncoughtExceptionHandler());
                try {
                    eventLoop();
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "Sheet service event loop interrupted.", e);
                }
            }
        };
        eventLoop.start();

        //set session.
        onSessionChanged(client.getSessionId());
        queue.add(new SendCarConnectedEvent(new LinkedList<>(client.getModel().getCarsInfo().values())));
    }

    public void stop() {
        if (running) {
            LOG.info("Stopping google sheets service");
            queue.add(new QuitEvent());
        }
    }

    public String getCurrentTargetSheet() {
        return currentSheetTarget;
    }

    public void setCurrentTargetSheet(String sheet) {
        this.currentSheetTarget = sheet;
        panel.invalidate();
    }

    public long getGreenFlagTimeStamp() {
        return greenFlagOffsetTimestamp;
    }

    public boolean isGreenFlagOffsetBeeingMeasured() {
        return isMeasuringGreenFlagOffset;
    }

    public boolean isRunning() {
        return running;
    }

    private void eventLoop() throws InterruptedException {
        while (running) {
            Object o = queue.take();
            if (o instanceof QuitEvent) {
                running = false;
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
    }

    private void sendIncident(SendIncidentEvent event) {

        String sheet = currentSheetTarget;
        String rangeSession = sheet + configuration.getFindEmptyRowRange();

        List<List<Object>> values;
        try {
            values = sheetService.getCells(rangeSession);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error getting spreadsheet values", e);
            return;
        }
        int emptyLine = values.size() + 1;

        rangeSession = sheet + configuration.getSessionTimeColumn() + emptyLine;
        String rangeCars = sheet + configuration.getCarInfoColumn() + emptyLine;

        List<List<Object>> lineSession = new LinkedList<>();
        lineSession.add(Arrays.asList(event.sessionTime));
        List<List<Object>> lineCars = new LinkedList<>();
        lineCars.add(Arrays.asList(event.carsInvolved));
        try {
            sheetService.updateCells(rangeSession, lineSession);
            sheetService.updateCells(rangeCars, lineCars);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error sending spreadsheet values", e);
        }
    }

    private void sendCarEntries(SendCarConnectedEvent event) {

        String sheet = "'entry list'!";
        String range = sheet + "B1:C500";

        List<List<Object>> values;
        try {
            values = sheetService.getCells(range);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error getting spreadsheet values", e);
            return;
        }
        Map<String, CarInfo> leftToAdd = new HashMap<>();
        for (CarInfo car : event.carsConnected) {
            String drivers = car.getDrivers().stream()
                    .map(driver -> driver.getFirstName() + " " + driver.getLastName())
                    .collect(Collectors.joining("\n"));
            leftToAdd.put(drivers, car);
        }

        //update previous entries
        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (!row.isEmpty()) {
                String name = (String) row.get(0);
                //search new car connections for this entry
                if (leftToAdd.containsKey(name)) {
                    row = Arrays.asList(name, leftToAdd.get(name).getCarNumber());
                    leftToAdd.remove(name);
                }
            }
            values.set(i, row);
        }

        //add remaining entries
        for (String key : leftToAdd.keySet()) {
            values.add(Arrays.asList(key, leftToAdd.get(key).getCarNumber()));
        }

        try {
            sheetService.updateCells(range, values);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error sending spreadsheet values", e);
        }
    }

    private void sendGreenFlag(GreenFlagEvent event) {
        String sheet = currentSheetTarget;
        String range = sheet + configuration.getReplayOffsetCell();
        try {
            sheetService.updateCells(range, Arrays.asList(Arrays.asList(TimeUtils.asDuration(event.time))));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error setting spreadsheet value", e);
        }
    }

    public LPContainer getPanel() {
        return panel;
    }

    private class SendIncidentEvent {

        public String sessionTime;
        public String carsInvolved;

        public SendIncidentEvent(String sessionTime, String carsInvolved) {
            this.sessionTime = sessionTime;
            this.carsInvolved = carsInvolved;
        }
    }

    private class SendCarConnectedEvent {

        public final List<CarInfo> carsConnected;

        public SendCarConnectedEvent(List<CarInfo> carsConnected) {
            this.carsConnected = new LinkedList<>(carsConnected);
        }
    }

    private class QuitEvent {
    }

    private class GreenFlagEvent {

        public int time;

        public GreenFlagEvent(int time) {
            this.time = time;
        }
    }
}
