/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.settings.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.AccConnection;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_COMMAND_PW;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_IP;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_PASSWORD;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_PORT;

/**
 *
 * Controlls the {@link ConnectionPanel}.
 *
 * @author Leonard
 */
public class ConnectionController
        implements EventListener {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(ConnectionController.class.getName());
    /**
     * The panel for this controller.
     */
    private final ConnectionPanel panel = new ConnectionPanel();
    /**
     * Client for game connection.
     */
    private final AccBroadcastingClient client;

    public ConnectionController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();

        panel.connectButton.setAction(this::connectButtonPressed);
    }

    public LPContainer getPanel() {
        return panel;
    }

    private void connectButtonPressed() {
        LOG.info("Connect button pressed");
        if (panel.autoDetectSettings) {
            connectAutomatic();
        } else {
            connectWithInput();
        }

    }

    private void connectAutomatic() {
        LOG.info("Try connecting with automatic settings");

        ConnectionObject connectionData;
        String filename = System.getProperty("user.home")
                + "/Documents/Assetto Corsa Competizione/Config/broadcasting.json";

        try {
            ObjectMapper mapper = new ObjectMapper();
            connectionData = mapper.readValue(new File(filename),
                    ConnectionObject.class);
        } catch (IOException e) {
            LOG.info("Configuration file not found at " + filename
                    + ". Switching to input mode");
            JOptionPane.showMessageDialog(null,
                    "Cannot find broadcasting configuration file. Please input connection settings manually",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
            panel.autoDetectSettingsCheckBox.setSelected(false);
            panel.setDisconnected();
            return;
        }

        try {
            client.connect("ACC Live timing",
                    connectionData.connectionPassword,
                    connectionData.commandPassword,
                    100,
                    InetAddress.getByName("127.0.0.1"),
                    connectionData.port);

        } catch (SocketException | UnknownHostException e) {
            LOG.log(Level.SEVERE, "Error starting the connection to the game.", e);
        }
        // set ui elements to reflect values that were used
        panel.ipTextField.setValue("127.0.0.1");
        panel.portTextField.setValue(String.valueOf(connectionData.port));
        panel.connectionPWTextField.setValue(connectionData.connectionPassword);
        panel.commandPWTextField.setValue(connectionData.commandPassword);
        //client.sendRegisterRequest();
    }

    private void connectWithInput() {
        LOG.info("Try connecting with input");
        InetAddress hostAddress;
        try {
            hostAddress = InetAddress.getByName(panel.ipTextField.getValue());
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, panel.ipTextField.getValue() + " is not a valid ip address.");
            return;
        }

        int hostPort;
        try {
            hostPort = Integer.valueOf(panel.portTextField.getValue());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, panel.portTextField.getValue() + " is not a valid port.");
            return;
        }

        int updateInterval = 100;

        try {
            client.connect("ACC Live timing",
                    panel.connectionPWTextField.getValue(),
                    panel.commandPWTextField.getValue(),
                    updateInterval,
                    hostAddress,
                    hostPort);

        } catch (SocketException e) {
            LOG.log(Level.SEVERE, "Error starting the connection to the game.", e);
        }
        //client.sendRegisterRequest();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionOpenedEvent) {
            RaceControlApplet.runLater(() -> {
                connectionOpenedEvent();
            });
        } else if (e instanceof ConnectionClosedEvent) {
            RaceControlApplet.runLater(() -> {
                connectionClosedEvent((ConnectionClosedEvent) e);
            });
        }
    }

    private void connectionOpenedEvent() {
        panel.setConnected();
        panel.connectButton.setAction(this::disconnectButtonPressed);
        //save config
        PersistantConfig.put(CONNECTION_IP, panel.ipTextField.getValue());
        PersistantConfig.put(CONNECTION_PORT, panel.portTextField.getValue());
        PersistantConfig.put(CONNECTION_PASSWORD, panel.connectionPWTextField.getValue());
        PersistantConfig.put(CONNECTION_COMMAND_PW, panel.commandPWTextField.getValue());
    }

    private void connectionClosedEvent(ConnectionClosedEvent event) {
        if (event.getExitState() != AccConnection.ExitState.USER) {
            showErrorMessage(event.getExitState());
        }
        panel.setDisconnected();
        panel.connectButton.setAction(this::connectButtonPressed);
    }

    private void disconnectButtonPressed() {
        if (client.isConnected()) {
            client.sendUnregisterRequest();
            client.stopAndKill();
        }
    }

    private void showErrorMessage(AccConnection.ExitState exitStatus) {
        if (exitStatus == AccConnection.ExitState.PORT_UNREACHABLE) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to game. The game needs to be on track to connect.",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (exitStatus == AccConnection.ExitState.REFUSED) {
            JOptionPane.showMessageDialog(null,
                    "Connection refused by the game. Wrong password.",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (exitStatus == AccConnection.ExitState.EXCEPTION) {
            JOptionPane.showMessageDialog(null,
                    "Unknown error while connecting to game",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
