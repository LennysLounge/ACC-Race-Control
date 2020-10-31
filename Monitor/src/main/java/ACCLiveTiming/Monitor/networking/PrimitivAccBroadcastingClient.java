/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.networking;

import ACCLiveTiming.monitor.networking.data.AccBroadcastingData;
import ACCLiveTiming.monitor.networking.data.BroadcastingEvent;
import ACCLiveTiming.monitor.networking.data.CarInfo;
import ACCLiveTiming.monitor.networking.data.RealtimeInfo;
import ACCLiveTiming.monitor.networking.data.SessionInfo;
import ACCLiveTiming.monitor.networking.data.TrackInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic connection to the broadcasting interface from Assetto Corsa
 * Competizione.
 *
 * @author Leonard
 */
public class PrimitivAccBroadcastingClient {

    private static final Logger LOG = Logger.getLogger(PrimitivAccBroadcastingClient.class.getName());

    /**
     * Address where the ACC broadcast server is running.
     */
    private final InetAddress hostAddress;
    /**
     * Port where the ACC broadcast server is running.
     */
    private final int hostPort;
    /**
     * Display name of this connection.
     */
    private final String displayName;
    /**
     * Connection password.
     */
    private final String connectionPassword;
    /**
     * Command password.
     */
    private final String commandPassword;
    /**
     * Interval in which to receive updated in ms.
     */
    private final int updateInterval;
    /**
     * Socket used for the connection.
     */
    private final DatagramSocket socket;
    /**
     * Thread where the connection loop is running.
     */
    private Thread accListenerThread;
    /**
     * Model that holds the data.
     */
    private AccBroadcastingData model = new AccBroadcastingData();
    /**
     * Protocoll used to communicate with ACC.
     */
    private final AccBroadcastingProtocol protocol = new AccBroadcastingProtocol(this);
    /**
     * Time when the entry list was requested.
     */
    private long lastTimeEntryListRequest = 0;
    /**
     * Flag to show that the socket was closed forcefully.
     */
    private boolean forceSocketClose = false;

    /**
     * Default contructor.
     *
     * @param displayName The display name of this connection.
     * @param connectionPassword The password for this connection.
     * @param commandPassword The command password.
     * @param updateInterval The interval in which to receive updates.
     * @param hostAddress Host address of the server.
     * @param hostPort Host port of the server.
     * @throws java.net.SocketException
     */
    public PrimitivAccBroadcastingClient(String displayName,
            String connectionPassword,
            String commandPassword,
            int updateInterval,
            InetAddress hostAddress,
            int hostPort) throws SocketException {
        this.displayName = requireNonNull(displayName, "displayName");
        this.connectionPassword = requireNonNull(connectionPassword, "connectionPassword");
        this.commandPassword = requireNonNull(commandPassword, "commandPassword");
        if (updateInterval < 0) {
            throw new IllegalArgumentException("Update interval cannot be less than 0");
        }
        this.updateInterval = updateInterval;
        this.hostAddress = requireNonNull(hostAddress, "hostAddress");
        this.hostPort = requireNonNull(hostPort, "hostPort");

        socket = new DatagramSocket();
        startListernerThread();
    }

    private void startListernerThread() {
        forceSocketClose = false;

        socket.connect(this.hostAddress, this.hostPort);
        accListenerThread = new Thread("ACC listener") {
            public void run() {
                LOG.info("Starting Listener thread");
                try {
                    udpListener();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error in the listener thread", e);
                } catch (StackOverflowError e) {
                    LOG.log(Level.SEVERE, "Overflow in listener thread", e);
                }
                LOG.info("Listener thread done");
            }
        };
        accListenerThread.start();
    }

    public void waitForFinish() {
        try {
            accListenerThread.join();
        } catch (InterruptedException ex) {
            LOG.info("exceptioN!!!!");
        }
    }

    /**
     * Gives the update interval for the connection.
     *
     * @return The update interval.
     */
    public int getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Returns the current model.
     *
     * @return The current data model.
     */
    public AccBroadcastingData getModel() {
        return model;
    }

    /**
     * Returns true if the socket is connected and currently listening for a
     * packet.
     *
     * @return True when connected.
     */
    public boolean isConnected() {
        return socket.isConnected() && accListenerThread.isAlive();
    }

    /**
     * Send a register command.
     *
     */
    public void sendRegisterRequest() {
        sendRequest(protocol.buildRegisterRequest(displayName, connectionPassword, updateInterval, commandPassword));
    }

    /**
     * Send unregister command.
     *
     */
    public void sendUnregisterRequest() {
        sendRequest(protocol.buildUnregisterRequest());
    }

    /**
     * Send a request for the current entry list.
     *
     */
    public void sendEntryListRequest() {
        lastTimeEntryListRequest = System.currentTimeMillis();
        sendRequest(protocol.buildEntryListRequest(model.getConnectionID()));
    }

    /**
     * Send a request for the current track data.
     *
     */
    public void sendTrackDataRequest() {
        sendRequest(protocol.buildTrackDataRequest(model.getConnectionID()));
    }

    /**
     * Disconnect from the game.
     */
    public void disconnect() {
        forceSocketClose = true;
        socket.disconnect();
        socket.close();
    }

    private void sendRequest(byte[] requestBytes) {
        if (socket.isConnected()) {
            try {
                socket.send(new DatagramPacket(requestBytes, requestBytes.length));
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error sending request.", e);
            }
        }
    }

    // Threading
    private void udpListener() {
        while (true) {
            try {
                DatagramPacket response = new DatagramPacket(new byte[512], 512);
                socket.receive(response);
                protocol.processMessage(new ByteArrayInputStream(response.getData()));
                afterPacketReceived(response.getData()[0]);
            } catch (SocketException e) {
                LOG.log(Level.SEVERE, "Socket closed unexpected.", e);
                return;
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while receiving a response", e);
                return;
            }
        }
    }

    protected void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message) {
        if (success == false) {
            LOG.info("Connection refused\n" + message);
        }
        model = model.withConnectionId(connectionID);

        try {
            sendEntryListRequest();
            sendTrackDataRequest();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while sending entrylist and trackdata request", e);
        }
    }

    protected void onRealtimeUpdate(SessionInfo sessionInfo) {
        model = model.withSessionInfo(sessionInfo);
    }

    protected void onRealtimeCarUpdate(RealtimeInfo info) {
        if (model.getCarsInfo().containsKey(info.getCarId())) {
            CarInfo car = model.getCarsInfo().get(info.getCarId());
            car = car.withRealtime(info);

            Map<Integer, CarInfo> cars = new HashMap<>();
            cars.putAll(model.getCarsInfo());
            cars.put(car.getCarId(), car);
            model = model.withCars(cars);
        } else {
            long now = System.currentTimeMillis();
            if (now - lastTimeEntryListRequest > 5000) {
                try {
                    sendEntryListRequest();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error while sending entrylist request", e);
                }
            }
        }
    }

    protected void onEntryListUpdate(List<Integer> carIds) {
        Map<Integer, CarInfo> cars = new HashMap<>();
        cars.putAll(model.getCarsInfo());
        //disconnect all connected cars that are not in this update.
        cars.values().stream()
                .filter(carInfo -> carInfo.isConnected())
                .filter(carInfo -> !carIds.contains(carInfo.getCarId()))
                .forEach(carInfo
                        -> cars.put(carInfo.getCarId(), carInfo.withConnected(false))
                );
        //add any new carIds.
        for (int carId : carIds) {
            if (!cars.containsKey(carId)) {
                cars.put(carId, new CarInfo());
            }
        }
        model = model.withCars(cars);
    }

    protected void onTrackData(TrackInfo info) {
        model = model.withTrackInfo(info);
    }

    protected void onEntryListCarUpdate(CarInfo carInfo) {
        Map<Integer, CarInfo> cars = new HashMap<>();
        cars.putAll(model.getCarsInfo());

        cars.put(carInfo.getCarId(), carInfo);
        model = model.withCars(cars);
    }

    protected void onBroadcastingEvent(BroadcastingEvent event) {
        List<BroadcastingEvent> events = new LinkedList<>();
        events.addAll(model.getEvents());
        events.add(event);

        model = model.withEvents(events);
    }

    protected void afterPacketReceived(byte type) {
    }
}
