/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import racecontrol.utility.Version;
import racecontrol.persistance.PersistantConfig;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.LPTextField;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_INTERVAL;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_IP;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_PASSWORD;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_PORT;

/**
 *
 * @author Leonard
 */
public class SettingsPanel
        extends LPContainer
        implements EventListener {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(SettingsPanel.class.getName());
    /**
     * Client for game connection.
     */
    private final AccBroadcastingClient client;

    private final LPLabel connectionHeading = new LPLabel("Connection Settings:");
    private final LPLabel ipLabel = new LPLabel("IP:");
    private final LPTextField ipTextField = new LPTextField();
    private final LPLabel portLabel = new LPLabel("Port:");
    private final LPTextField portTextField = new LPTextField();
    private final LPLabel connectionPWLabel = new LPLabel("Connection PW:");
    private final LPTextField connectionPWTextField = new LPTextField();
    private final LPLabel updateIntervalLabel = new LPLabel("Update Interval:");
    private final LPTextField updateIntervalTextField = new LPTextField();
    private final LPButton connectButton = new LPButton("Connect");


    private final LPLabel versionLabel = new LPLabel("Version: " + Version.VERSION);

    public SettingsPanel() {
        setName("CONFIGURATION");
        this.client = AccBroadcastingClient.getClient();

        EventBus.register(this);

        initComponents();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionClosedEvent) {
            connectionClosedEvent((ConnectionClosedEvent) e);
        }
    }

    private void connectionClosedEvent(ConnectionClosedEvent event) {
        if (event.getExitState() != AccBroadcastingClient.ExitState.NORMAL) {
            showErrorMessage(event.getExitState());
        }
        connectButton.setText("Connect");
        ipTextField.setEnabled(true);
        portTextField.setEnabled(true);
        connectionPWTextField.setEnabled(true);
        updateIntervalTextField.setEnabled(true);
        connectButton.setAction(() -> connectButtonPressed());
    }

    private void connectButtonPressed() {
        LOG.info("Connect button pressed");
        InetAddress hostAddress;
        try {
            hostAddress = InetAddress.getByName(ipTextField.getValue());
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, ipTextField.getValue() + " is not a valid ip address.");
            return;
        }

        int hostPort;
        try {
            hostPort = Integer.valueOf(portTextField.getValue());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, portTextField.getValue() + " is not a valid port.");
            return;
        }

        int updateInterval;
        try {
            updateInterval = Integer.valueOf(updateIntervalTextField.getValue());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, updateIntervalTextField.getValue() + " is not a valid port.");
            return;
        }

        try {
            client.connect("ACC Live timing",
                    connectionPWTextField.getValue(),
                    "",
                    updateInterval,
                    hostAddress,
                    hostPort);

        } catch (SocketException e) {
            LOG.log(Level.SEVERE, "Error starting the connection to the game.", e);
        }

        client.sendRegisterRequest();

        connectButton.setText("Disconnect");
        ipTextField.setEnabled(false);
        portTextField.setEnabled(false);
        connectionPWTextField.setEnabled(false);
        updateIntervalTextField.setEnabled(false);
        connectButton.setAction(() -> disconnectButtonPressed());

        //save config
        PersistantConfig.put(CONNECTION_IP, ipTextField.getValue());
        PersistantConfig.put(CONNECTION_PORT, portTextField.getValue());
        PersistantConfig.put(CONNECTION_PASSWORD, connectionPWTextField.getValue());
        PersistantConfig.put(CONNECTION_INTERVAL, updateIntervalTextField.getValue());
    }

    private void disconnectButtonPressed() {
        if (client.isConnected()) {
            client.sendUnregisterRequest();
            client.stopAndKill();
        }
    }

    private void showErrorMessage(AccBroadcastingClient.ExitState exitStatus) {
        if (exitStatus == AccBroadcastingClient.ExitState.PORT_UNREACHABLE) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to game. The game needs to be on track to connect.",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (exitStatus == AccBroadcastingClient.ExitState.REFUSED) {
            JOptionPane.showMessageDialog(null,
                    "Connection refused by the game. Wrong password.",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (exitStatus == AccBroadcastingClient.ExitState.EXCEPTION) {
            JOptionPane.showMessageDialog(null,
                    "Unknown error while connecting to game",
                    "Error connecting to game",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.stroke(COLOR_GRAY);
        applet.line(420, 10, 420, getHeight() - 20);
        applet.noStroke();
    }

    private void initComponents() {
        connectionHeading.setSize(400, LookAndFeel.LINE_HEIGHT);
        connectionHeading.setHAlign(CENTER);
        addComponent(connectionHeading);

        addComponent(ipLabel);
        ipTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        ipTextField.setValue(PersistantConfig.get(CONNECTION_IP));
        addComponent(ipTextField);

        addComponent(portLabel);
        portTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        portTextField.setValue(PersistantConfig.get(CONNECTION_PORT));
        addComponent(portTextField);

        addComponent(connectionPWLabel);
        connectionPWTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        connectionPWTextField.setValue(PersistantConfig.get(CONNECTION_PASSWORD));
        addComponent(connectionPWTextField);

        addComponent(updateIntervalLabel);
        updateIntervalTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        updateIntervalTextField.setValue(PersistantConfig.get(CONNECTION_INTERVAL));
        addComponent(updateIntervalTextField);

        connectButton.setSize(380, LookAndFeel.LINE_HEIGHT);
        connectButton.setAction(() -> connectButtonPressed());
        addComponent(connectButton);

        addComponent(versionLabel);
    }

    @Override
    public void onResize(float w, float h) {
        int lh = (int) (LookAndFeel.LINE_HEIGHT * 1.2f);
        connectionHeading.setPosition(0, 0);

        ipLabel.setPosition(20, lh * 1);
        ipTextField.setPosition(200, lh * 1);

        portLabel.setPosition(20, lh * 2);
        portTextField.setPosition(200, lh * 2f);

        connectionPWLabel.setPosition(20, lh * 3);
        connectionPWTextField.setPosition(200, lh * 3f);

        updateIntervalLabel.setPosition(20, lh * 4);
        updateIntervalTextField.setPosition(200, lh * 4);

        connectButton.setPosition(20, lh * 5);

        versionLabel.setPosition(20, getHeight() - LINE_HEIGHT);
    }

}
