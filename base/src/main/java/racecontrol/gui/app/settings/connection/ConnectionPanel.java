/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.settings.connection;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.LPTextField;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_COMMAND_PW;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_IP;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_PASSWORD;
import static racecontrol.persistance.PersistantConfigKeys.CONNECTION_PORT;
import static racecontrol.persistance.PersistantConfigKeys.USE_AUTO_CONNECT_SETTINGS;

/**
 *
 * Presents the connection settings and the connect button.
 *
 * @author Leonard
 */
public class ConnectionPanel
        extends LPContainer {

    private final LPLabel connectionHeading = new LPLabel("Connection Settings:");
    private final LPLabel ipLabel = new LPLabel("IP:");
    protected final LPTextField ipTextField = new LPTextField();
    private final LPLabel portLabel = new LPLabel("Port:");
    protected final LPTextField portTextField = new LPTextField();
    private final LPLabel connectionPWLabel = new LPLabel("Connection PW:");
    protected final LPTextField connectionPWTextField = new LPTextField();
    private final LPLabel commandPWLabel = new LPLabel("Command PW:");
    protected final LPTextField commandPWTextField = new LPTextField();
    protected final LPCheckBox autoDetectSettingsCheckBox = new LPCheckBox();
    private final LPLabel autoDetectSettingsLabel = new LPLabel("Auto detect connection settings");
    protected final LPButton connectButton = new LPButton("Connect");

    protected boolean autoDetectSettings = false;

    public ConnectionPanel() {
        initComponents();
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

        addComponent(commandPWLabel);
        commandPWTextField.setSize(200, LookAndFeel.LINE_HEIGHT);
        commandPWTextField.setValue(PersistantConfig.get(CONNECTION_COMMAND_PW));
        addComponent(commandPWTextField);

        addComponent(autoDetectSettingsLabel);
        autoDetectSettingsCheckBox.setSize(LINE_HEIGHT, LINE_HEIGHT);
        autoDetectSettings = PersistantConfig.get(USE_AUTO_CONNECT_SETTINGS);
        autoDetectSettingsCheckBox.setSelected(autoDetectSettings);
        autoDetectSettingsCheckBox.setChangeAction(this::autoDetectSettingsFlipped);
        addComponent(autoDetectSettingsCheckBox);

        connectButton.setSize(380, LookAndFeel.LINE_HEIGHT);
        addComponent(connectButton);

        setSize(400, LINE_HEIGHT * 1.2f * 7);
        setDisconnected();
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        int lh = (int) (LookAndFeel.LINE_HEIGHT * 1.2f);
        connectionHeading.setPosition(0, 0);

        ipLabel.setPosition(20, lh * 1f);
        ipTextField.setPosition(200, lh * 1f);

        portLabel.setPosition(20, lh * 2f);
        portTextField.setPosition(200, lh * 2f);

        connectionPWLabel.setPosition(20, lh * 3f);
        connectionPWTextField.setPosition(200, lh * 3f);

        commandPWLabel.setPosition(20, lh * 4f);
        commandPWTextField.setPosition(200, lh * 4f);

        autoDetectSettingsLabel.setPosition(20, lh * 5f);
        autoDetectSettingsCheckBox.setPosition(400 - lh,
                lh * 5f + (LINE_HEIGHT - TEXT_SIZE) / 2f);

        connectButton.setPosition(20, lh * 6);
    }

    /**
     * Sets the panel into a state where the application is connected. This
     * disables all input fields.
     */
    public void setConnected() {
        ipTextField.setEnabled(false);
        ipLabel.setEnabled(false);
        portTextField.setEnabled(false);
        portLabel.setEnabled(false);
        connectionPWTextField.setEnabled(false);
        connectionPWLabel.setEnabled(false);
        commandPWTextField.setEnabled(false);
        commandPWLabel.setEnabled(false);
        autoDetectSettingsCheckBox.setEnabled(false);
        autoDetectSettingsLabel.setEnabled(false);
        connectButton.setText("Disconnect");
    }

    /**
     * Sets the panel into a state where the application is disconnected. This
     * enables all input fields.
     */
    public void setDisconnected() {
        ipTextField.setEnabled(!autoDetectSettings);
        ipLabel.setEnabled(!autoDetectSettings);
        portTextField.setEnabled(!autoDetectSettings);
        portLabel.setEnabled(!autoDetectSettings);
        connectionPWTextField.setEnabled(!autoDetectSettings);
        connectionPWLabel.setEnabled(!autoDetectSettings);
        commandPWTextField.setEnabled(!autoDetectSettings);
        commandPWLabel.setEnabled(!autoDetectSettings);
        autoDetectSettingsCheckBox.setEnabled(true);
        autoDetectSettingsLabel.setEnabled(true);
        connectButton.setText("Connect");
    }

    public void autoDetectSettingsFlipped(boolean state) {
        autoDetectSettings = state;
        PersistantConfig.put(USE_AUTO_CONNECT_SETTINGS, state);
        setDisconnected();
    }

}
