/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.visualisation.components;

import base.ACCLiveTimingExtensionFactory;
import base.Version;
import base.persistance.PersistantConfig;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.GeneralExtentionConfigPanel;
import base.screen.networking.AccBroadcastingClient;
import base.screen.networking.events.ConnectionClosed;
import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.COLOR_GRAY;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import base.screen.visualisation.Visualisation;
import base.screen.visualisation.gui.LPButton;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPLabel;
import base.screen.visualisation.gui.LPTabPanel;
import base.screen.visualisation.gui.LPTextField;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class ConfigPanel
        extends LPContainer
        implements EventListener {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(ConfigPanel.class.getName());
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

    private final LPLabel extensionHeading = new LPLabel("Extension Settings:");
    private final LPTabPanel extensionTabPanel = new LPTabPanel();

    private final LPLabel versionLabel = new LPLabel("Version: " + Version.VERSION);

    public ConfigPanel(AccBroadcastingClient client) {
        setName("Configuration");
        this.client = client;

        EventBus.register(this);

        initComponents();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionClosed) {
            connectionClosedEvent((ConnectionClosed) e);
        }
    }

    private void connectionClosedEvent(ConnectionClosed event) {
        if (event.getExitState() != AccBroadcastingClient.ExitState.NORMAL) {
            showErrorMessage(event.getExitState());
        }
        connectButton.setText("Connect");
        ipTextField.setEnabled(true);
        portTextField.setEnabled(true);
        connectionPWTextField.setEnabled(true);
        updateIntervalTextField.setEnabled(true);
        connectButton.setAction(() -> connectButtonPressed());

        //enable config dialogs.
        extensionTabPanel.getTabs().stream()
                .forEach(tab -> tab.setEnabled(true));
    }

    private void connectButtonPressed() {
        //Remove all current extension.
        Visualisation.getModules().stream()
                .forEach(module -> module.removeExtension());

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

        //Create new extensions.
        Visualisation.getModules().stream()
                .forEach(module -> module.createExtension());

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

        //disable all config dialogs.
        extensionTabPanel.getTabs().stream()
                .forEach(tab -> tab.setEnabled(false));

        //save config
        PersistantConfig.setConnectionIP(ipTextField.getValue());
        PersistantConfig.setConnectionPort(portTextField.getValue());
        PersistantConfig.setConnectionPassword(connectionPWTextField.getValue());
        PersistantConfig.setConnectionInterval(updateIntervalTextField.getValue());
    }

    private void disconnectButtonPressed() {
        if (client.isConnected()) {
            client.sendUnregisterRequest();
            client.stopAndKill();
        }
    }

    private static void showErrorMessage(AccBroadcastingClient.ExitState exitStatus) {
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
    public void draw() {
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
        ipTextField.setValue(PersistantConfig.getConnectionIP());
        addComponent(ipTextField);

        addComponent(portLabel);
        portTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        portTextField.setValue(PersistantConfig.getConnectionIPort());
        addComponent(portTextField);

        addComponent(connectionPWLabel);
        connectionPWTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        connectionPWTextField.setValue(PersistantConfig.getConnectionPassword());
        addComponent(connectionPWTextField);

        addComponent(updateIntervalLabel);
        updateIntervalTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        updateIntervalTextField.setValue(PersistantConfig.getConnectionInterval());
        addComponent(updateIntervalTextField);

        connectButton.setSize(380, LookAndFeel.LINE_HEIGHT);
        connectButton.setAction(() -> connectButtonPressed());
        addComponent(connectButton);

        extensionHeading.setSize(getWidth() - 420, LookAndFeel.LINE_HEIGHT);
        addComponent(extensionHeading);
        extensionTabPanel.setActiveTabColor(COLOR_GRAY);
        addComponent(extensionTabPanel);

        extensionTabPanel.addTab(GeneralExtentionConfigPanel.getInstance());
        for (ACCLiveTimingExtensionFactory module : Visualisation.getModules()) {
            LPContainer configurationPanel = module.getExtensionConfigurationPanel();
            if (configurationPanel != null) {
                extensionTabPanel.addTab(configurationPanel);
            }
        }
        extensionTabPanel.setTabIndex(0);

        addComponent(versionLabel);
    }

    @Override
    public void onResize(int w, int h) {
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

        extensionHeading.setPosition(440, 0);
        extensionHeading.setSize(w - 460, LookAndFeel.LINE_HEIGHT);
        extensionTabPanel.setPosition(440, lh);
        extensionTabPanel.setSize(w - 460, h - lh - 20);

        versionLabel.setPosition(20, getHeight() - LINE_HEIGHT);
    }

}
