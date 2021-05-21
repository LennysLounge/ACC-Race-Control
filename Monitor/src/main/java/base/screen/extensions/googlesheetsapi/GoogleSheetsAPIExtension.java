/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.googlesheetsapi;

import base.screen.Main;
import base.screen.networking.SessionId;
import base.screen.networking.events.SessionPhaseChanged;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.incidents.IncidentInfo;
import base.screen.extensions.incidents.events.Accident;
import base.screen.extensions.logging.LoggingExtension;
import base.screen.networking.AccBroadcastingClient;
import base.screen.networking.RealtimeUpdate;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.enums.SessionPhase;
import base.screen.networking.enums.SessionType;
import base.screen.networking.events.CarConnect;
import base.screen.networking.events.SessionChanged;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.gui.LPContainer;
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

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtension
        implements EventListener, AccClientExtension {

    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIExtension.class.getName());

    /**
     * The Sheet service for the spreadsheet api.
     */
    private final GoogleSheetsService sheetService;
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

    private final AccBroadcastingClient client;

    private String findEmptyRowRange = GoogleSheetsAPIConfigurationPanel.FIND_EMPTY_ROW_RANGE;
    private String replayOffsetCell = GoogleSheetsAPIConfigurationPanel.REPLAY_OFFSET_CELL;
    private String sessionColumn = GoogleSheetsAPIConfigurationPanel.SESSION_TIME_COLUMN;
    private String carInfoColumn = GoogleSheetsAPIConfigurationPanel.CAR_INFO_COLUMN;
    /**
     * List of recently connected cars to be send to the entry list.
     */
    private List<CarInfo> carConnections = new LinkedList<>();

    public GoogleSheetsAPIExtension(GoogleSheetsService service) {
        EventBus.register(this);
        panel = new GoogleSheetsAPIPanel(this);
        client = Main.getClient();
        this.sheetService = service;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            currentSessionId = ((SessionChanged) e).getSessionId();
            setCurrentTargetSheet(getTargetSheet(currentSessionId));
            LOG.info("Target Sheet changed to \"" + currentSheetTarget + "\"");
            LoggingExtension.log("Spreasheet target changed to \"" + currentSheetTarget + "\"");
        } else if (e instanceof Accident) {
            IncidentInfo info = ((Accident) e).getInfo();
            String sessionTime = TimeUtils.asDuration(info.getSessionEarliestTime());
            String carNumbers = info.getCars().stream()
                    .map(car -> getCarNumberAndLapCount(car))
                    .collect(Collectors.joining("\n"));
            queue.add(new SendIncidentEvent(sessionTime, carNumbers));
            LOG.info("accident received: " + carNumbers);
        } else if (e instanceof SessionPhaseChanged) {
            SessionInfo info = ((SessionPhaseChanged) e).getSessionInfo();
            if (info.getSessionType() == SessionType.RACE) {
                if (info.getPhase() == SessionPhase.STARTING) {
                    isMeasuringGreenFlagOffset = true;
                    greenFlagOffsetTimestamp = System.currentTimeMillis();
                } else if (info.getPhase() == SessionPhase.SESSION && isMeasuringGreenFlagOffset) {
                    long diff = System.currentTimeMillis() - greenFlagOffsetTimestamp;
                    queue.add(new GreenFlagEvent((int) diff));
                    isMeasuringGreenFlagOffset = false;
                }
            }
        } else if (e instanceof CarConnect) {
            carConnections.add(((CarConnect) e).getCar());
        } else if (e instanceof RealtimeUpdate) {
            if (!carConnections.isEmpty()) {
                queue.add(new SendCarConnectedEvent(carConnections));
                carConnections.clear();
            }
        }
        if (isGreenFlagOffsetBeeingMeasured()) {
            panel.invalidate();
        }
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

    public void start() {
        //Start event loop
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

    public void setFindEmptyRowRange(String findEmptyRowRange) {
        this.findEmptyRowRange = findEmptyRowRange;
    }

    public void setReplayOffsetCell(String replayOffsetCell) {
        this.replayOffsetCell = replayOffsetCell;
    }

    public void setSessionColumn(String sessionColumn) {
        this.sessionColumn = sessionColumn;
    }

    public void setCarInfoColumn(String carInfoColumn) {
        this.carInfoColumn = carInfoColumn;
    }

    private void eventLoop() throws InterruptedException {
        while (running) {
            Object o = queue.take();
            if (o instanceof QuitEvent) {
                running = false;
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
        String rangeSession = sheet + findEmptyRowRange;

        List<List<Object>> values;
        try {
            values = sheetService.getCells(rangeSession);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error getting spreadsheet values", e);
            return;
        }
        int emptyLine = values.size() + 1;

        rangeSession = sheet + sessionColumn + emptyLine;
        String rangeCars = sheet + carInfoColumn + emptyLine;

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
            String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
            leftToAdd.put(name, car);
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
        String range = sheet + replayOffsetCell;
        try {
            sheetService.updateCells(range, Arrays.asList(Arrays.asList(TimeUtils.asDuration(event.time))));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error setting spreadsheet value", e);
        }
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void removeExtension() {
        EventBus.unregister(this);
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
