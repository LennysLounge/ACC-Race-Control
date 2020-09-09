/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.networking;

import ACCLiveTiming.networking.data.AccBroadcastingData;
import ACCLiveTiming.networking.data.BroadcastingEvent;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.data.SessionInfo;
import ACCLiveTiming.networking.data.TrackInfo;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic connection to the broadcasting interface from Assetto Corsa
 * Competizione.
 *
 * @author Leonard
 */
public class PrimitivAccBroadcastingClient {

    private static Logger LOG = Logger.getLogger(PrimitivAccBroadcastingClient.class.getName());

    /**
     * Address where the ACC broadcast server is running.
     */
    private InetAddress hostAddress;
    /**
     * Port where the ACC broadcast server is running.
     */
    private int hostPort;
    /**
     * Display name of this connection.
     */
    private String displayName = "Your Name";
    /**
     * Connection password.
     */
    private String connectionPassword = "";
    /**
     * Command password.
     */
    private String commandPassword = "";
    /**
     * Interval in which to receive updated in ms.
     */
    private int updateInterval = 250;
    /**
     * Socket used for the connection.
     */
    private DatagramSocket socket;
    /**
     * Thread where the connection loop is running.
     */
    private Thread udpListenerThread;
    /**
     * Model that holds the data.
     */
    private AccBroadcastingData model = new AccBroadcastingData();
    /**
     * Protocoll used to communicate with ACC.
     */
    private AccBroadcastingProtocol protocol = new AccBroadcastingProtocol(this);
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
     */
    public PrimitivAccBroadcastingClient() throws SocketException {
        socket = new DatagramSocket();
    }

    /**
     * Set the credentials for the ACC connection.
     *
     * @param name The display name.
     * @param password The connection password.
     * @param commandPassword The command password.
     */
    public void setCredentials(String name, String password, String commandPassword) {
        this.displayName = name;
        this.connectionPassword = password;
        this.commandPassword = commandPassword;
    }

    /**
     * Sets the update interval for this connection.
     *
     * @param interval The interval.
     */
    public void setUpdateInterval(int interval) {
        this.updateInterval = interval;
    }
    /**
     * Gives the update interval for the connection.
     * @return The update interval.
     */
    public int getUpdateInterval(){
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
        return socket.isConnected() && udpListenerThread.isAlive();
    }

    /**
     * Send a register command.
     *
     * @throws IOException
     */
    public void sendRegisterRequest() throws IOException {
        sendRequest(protocol.buildRegisterRequest(displayName, connectionPassword, updateInterval, commandPassword));
    }

    /**
     * Send unregister command.
     *
     * @throws IOException
     */
    public void sendUnregisterRequest() throws IOException {
        sendRequest(protocol.buildUnregisterRequest());
    }

    /**
     * Send a request for the current entry list.
     *
     * @throws IOException
     */
    public void sendEntryListRequest() throws IOException {
        lastTimeEntryListRequest = System.currentTimeMillis();
        sendRequest(protocol.buildEntryListRequest(model.getConnectionID()));
    }

    /**
     * Send a request for the current track data.
     *
     * @throws IOException
     */
    public void sendTrackDataRequest() throws IOException {
        sendRequest(protocol.buildTrackDataRequest(model.getConnectionID()));
    }

    /**
     * Connect the socket and start the listener thread.
     *
     * @param hostAddress Host address of the game.
     * @param hostPort Host port of the game.
     */
    public void connect(InetAddress hostAddress, int hostPort) {
        forceSocketClose = false;
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        socket.connect(this.hostAddress, this.hostPort);

        udpListenerThread = new Thread("UDP listener Thread") {
            public void run() {
                try {
                    udpListener();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error in the listener thread", e);
                } catch (StackOverflowError e) {
                    LOG.log(Level.SEVERE, "Overflow in listener", e);
                }
            }
        };
        udpListenerThread.start();
    }

    /**
     * Disconnect from the game.
     */
    public void disconnect() {
        forceSocketClose = true;
        socket.disconnect();
        socket.close();
    }

    private void sendRequest(byte[] requestBytes) throws IOException {
        if (socket.isConnected()) {
            socket.send(new DatagramPacket(requestBytes, requestBytes.length));
        }
    }

    // Threading
    private void udpListener() {
        LOG.info("Starting Listener thread");
        while (true) {
            try {
                DatagramPacket response = new DatagramPacket(new byte[512], 512);
                socket.receive(response);
                protocol.processMessage(new ByteArrayInputStream(response.getData()));
                afterPacketReceived(response.getData()[0]);
            } catch (SocketException e) {
                if (forceSocketClose) {
                    LOG.info("Socket closed");
                } else {
                    LOG.log(Level.SEVERE, "Socket closed unexpected.", e);
                }
                break;
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while receiving a response", e);
                break;
            }
        }
        LOG.info("Listener thread done");
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
