/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation.components;

import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfig.EXTENSION_BROADCSATING_ENABLED;
import static racecontrol.persistance.PersistantConfig.EXTENSION_CAMERA_CONTROL_ENABLED;
import static racecontrol.persistance.PersistantConfig.EXTENSION_INCIDENTS_ENABLED;
import static racecontrol.persistance.PersistantConfig.EXTENSION_LIVE_TIMING_ENABLED;
import static racecontrol.persistance.PersistantConfig.EXTENSION_LOGGING_ENABLED;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import static racecontrol.visualisation.LookAndFeel.TEXT_SIZE;
import racecontrol.visualisation.gui.LPCheckBox;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPLabel;

/**
 *
 * @author Leonard
 */
public class GeneralExtentionConfigPanel
        extends LPContainer {

    private final LPLabel headingLabel = new LPLabel("Settings for general features.");

    private final LPCheckBox liveTimingCheckBox = new LPCheckBox();
    private final LPLabel liveTimingLabel = new LPLabel("Enable live timing screen");

    private final LPCheckBox incidentLogCheckBox = new LPCheckBox();
    private final LPLabel incidentLogLabel = new LPLabel("Enable incidents screen");

    private final LPCheckBox loggingCheckBox = new LPCheckBox();
    private final LPLabel loggingLabel = new LPLabel("Enable logging screen");
    
    private final LPCheckBox cameraControlsCheckBox = new LPCheckBox();
    private final LPLabel cameraControlsLabel = new LPLabel("Enable camera controls");

    private final LPCheckBox velocityMapCheckBox = new LPCheckBox();
    private final LPLabel velocityMapLabel = new LPLabel("Enable velocity map");

    private final LPCheckBox trackMapCheckBox = new LPCheckBox();
    private final LPLabel trackMapLabel = new LPLabel("Enable track map");
    
    private final LPCheckBox broadcastingCheckBox = new LPCheckBox();
    private final LPLabel broadcastingLabel = new LPLabel("Enable Broadcasting control");

    /**
     * Singleton instance of this class.
     */
    private static GeneralExtentionConfigPanel instance = null;

    /**
     * privat constructor. To get an instance of this class use getInstance.
     */
    private GeneralExtentionConfigPanel() {
        setName("General");
        headingLabel.setPosition(20, 0);
        headingLabel.setSize(300, LINE_HEIGHT);
        addComponent(headingLabel);

        liveTimingCheckBox.setPosition(20, LINE_HEIGHT + (LINE_HEIGHT - TEXT_SIZE) / 2);
        liveTimingCheckBox.setSelected(PersistantConfig.getConfigBoolean(EXTENSION_LIVE_TIMING_ENABLED));
        addComponent(liveTimingCheckBox);
        liveTimingLabel.setPosition(60, LINE_HEIGHT);
        liveTimingLabel.setSize(300, LINE_HEIGHT);
        addComponent(liveTimingLabel);

        incidentLogCheckBox.setPosition(20, LINE_HEIGHT * 2 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        incidentLogCheckBox.setSelected(PersistantConfig.getConfigBoolean(EXTENSION_INCIDENTS_ENABLED));
        addComponent(incidentLogCheckBox);
        incidentLogLabel.setPosition(60, LINE_HEIGHT * 2);
        incidentLogLabel.setSize(300, LINE_HEIGHT);
        addComponent(incidentLogLabel);

        loggingCheckBox.setPosition(20, LINE_HEIGHT * 3 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        loggingCheckBox.setSelected(PersistantConfig.getConfigBoolean(EXTENSION_LOGGING_ENABLED));
        addComponent(loggingCheckBox);
        loggingLabel.setPosition(60, LINE_HEIGHT * 3);
        loggingLabel.setSize(300, LINE_HEIGHT);
        addComponent(loggingLabel);
        
        cameraControlsCheckBox.setPosition(20, LINE_HEIGHT * 4 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        cameraControlsCheckBox.setSelected(PersistantConfig.getConfigBoolean(EXTENSION_CAMERA_CONTROL_ENABLED));
        addComponent(cameraControlsCheckBox);
        cameraControlsLabel.setPosition(60, LINE_HEIGHT * 4);
        cameraControlsLabel.setSize(300, LINE_HEIGHT);
        addComponent(cameraControlsLabel);
        
        broadcastingCheckBox.setPosition(20, LINE_HEIGHT * 5 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        broadcastingCheckBox.setSelected(PersistantConfig.getConfigBoolean(EXTENSION_BROADCSATING_ENABLED));
        addComponent(broadcastingCheckBox);
        broadcastingLabel.setPosition(60, LINE_HEIGHT * 5);
        broadcastingLabel.setSize(300, LINE_HEIGHT);
        addComponent(broadcastingLabel);

        velocityMapCheckBox.setPosition(20, LINE_HEIGHT * 4 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        velocityMapCheckBox.setSelected(false);
        velocityMapCheckBox.setEnabled(true);
        //addComponent(velocityMapCheckBox);
        velocityMapLabel.setPosition(60, LINE_HEIGHT * 4);
        velocityMapLabel.setSize(300, LINE_HEIGHT);
        //addComponent(velocityMapLabel);

        trackMapCheckBox.setPosition(20, LINE_HEIGHT * 5 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        trackMapCheckBox.setSelected(false);
        trackMapCheckBox.setEnabled(false);
        //addComponent(trackMapCheckBox);
        trackMapLabel.setPosition(60, LINE_HEIGHT * 5);
        trackMapLabel.setSize(300, LINE_HEIGHT);
        trackMapLabel.setEnabled(false);
        //addComponent(trackMapLabel);
        
        
    }

    @Override
    public void onEnabled() {
        trackMapCheckBox.setEnabled(false);
        trackMapLabel.setEnabled(false);
        invalidate();
    }

    public static GeneralExtentionConfigPanel getInstance() {
        if (instance == null) {
            instance = new GeneralExtentionConfigPanel();
        }
        return instance;
    }

    public boolean isLiveTimingEnabled() {
        return liveTimingCheckBox.isSelected();
    }

    public boolean isIncidentLogEnabled() {
        return incidentLogCheckBox.isSelected();
    }

    public boolean isLoggingEnabled() {
        return loggingCheckBox.isSelected();
    }

    public boolean isVelocityMapEnabled() {
        return velocityMapCheckBox.isSelected();
    }
    
    public boolean isCameraControlsEnabled(){
        return cameraControlsCheckBox.isSelected();
    }
    
    public boolean isBroadcastingEnabled(){
        return broadcastingCheckBox.isSelected();
    }

}
