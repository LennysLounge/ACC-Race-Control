/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.virtualsafetycar;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.LPNumberTextField;

/**
 *
 * @author Leonard
 */
public class VirtualSafetyCarPanel
        extends LPContainer {

    protected final LPNumberTextField targetSpeedTextField = new LPNumberTextField();
    private final LPLabel targetSpeedLabel = new LPLabel("Speed limit:");
    private final LPLabel targetSpeedUnitLabel = new LPLabel("km/h");

    private final LPLabel speedToleranceLabel = new LPLabel("Speed tolerance:");
    protected final LPNumberTextField speedToleranceTextField = new LPNumberTextField();
    private final LPLabel speedToleranceUnitLabel = new LPLabel("km/h");
    //private final LPLabel speedToleranceInfoLabel1 = new LPLabel("Minimum speed a car needs to be above");
    //private final LPLabel speedToleranceInfoLabel2 = new LPLabel("the target speed to raise an incident");

    private final LPLabel timeToleranceLabel = new LPLabel("Time tolerance:");
    protected final LPNumberTextField timeToleranceTextField = new LPNumberTextField();
    private final LPLabel timeToleranceUnitLabel = new LPLabel("seconds");
    //private final LPLabel timeToleranceInfoLabel1 = new LPLabel("Minimum time a car needs to be above the");
    //private final LPLabel timeToleranceInfoLabel2 = new LPLabel("target speed to raise an incident.");

    protected final LPButton startButton = new LPButton("Start");
    protected final LPButton stopButton = new LPButton("Stop");

    protected boolean isVSCDisabled = true;

    public VirtualSafetyCarPanel() {
        setName("Virtual Safety Car");
        targetSpeedLabel.setPosition(10, LINE_HEIGHT * 0.5f);
        addComponent(targetSpeedLabel);
        targetSpeedTextField.setPosition(180, LINE_HEIGHT * 0.5f);
        targetSpeedTextField.setSize(70, LINE_HEIGHT);
        targetSpeedTextField.setValue(50);
        addComponent(targetSpeedTextField);
        targetSpeedUnitLabel.setPosition(260, LINE_HEIGHT * 0.5f);
        addComponent(targetSpeedUnitLabel);

        speedToleranceLabel.setPosition(10, LINE_HEIGHT * 2);
        addComponent(speedToleranceLabel);
        speedToleranceTextField.setPosition(180, LINE_HEIGHT * 2);
        speedToleranceTextField.setSize(70, LINE_HEIGHT);
        speedToleranceTextField.setValue(5);
        addComponent(speedToleranceTextField);
        speedToleranceUnitLabel.setPosition(260, LINE_HEIGHT * 2);
        addComponent(speedToleranceUnitLabel);
        /*
        speedToleranceInfoLabel1.setPosition(10, LINE_HEIGHT * 3);
        addComponent(speedToleranceInfoLabel1);
        speedToleranceInfoLabel2.setPosition(10, LINE_HEIGHT * 4);
        addComponent(speedToleranceInfoLabel2);
         */

        timeToleranceLabel.setPosition(10, LINE_HEIGHT * 3);
        addComponent(timeToleranceLabel);
        timeToleranceTextField.setPosition(180, LINE_HEIGHT * 3);
        timeToleranceTextField.setSize(70, LINE_HEIGHT);
        timeToleranceTextField.setValue(1);
        addComponent(timeToleranceTextField);
        timeToleranceUnitLabel.setPosition(260, LINE_HEIGHT * 3);
        addComponent(timeToleranceUnitLabel);
        /*
        timeToleranceInfoLabel1.setPosition(10, LINE_HEIGHT * 7);
        addComponent(timeToleranceInfoLabel1);
        timeToleranceInfoLabel2.setPosition(10, LINE_HEIGHT * 8);
        addComponent(timeToleranceInfoLabel2);
         */

        startButton.setPosition(10, LINE_HEIGHT * 4);
        startButton.setSize(120, LINE_HEIGHT);
        addComponent(startButton);
        stopButton.setPosition(220, LINE_HEIGHT * 4);
        stopButton.setSize(120, LINE_HEIGHT);
        addComponent(stopButton);

        setSize(350, LINE_HEIGHT * 5 + 10);
        updateComponents();
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    protected void updateComponents() {
        targetSpeedLabel.setEnabled(isVSCDisabled);
        targetSpeedTextField.setEnabled(isVSCDisabled);
        targetSpeedUnitLabel.setEnabled(isVSCDisabled);

        speedToleranceLabel.setEnabled(isVSCDisabled);
        speedToleranceTextField.setEnabled(isVSCDisabled);
        speedToleranceUnitLabel.setEnabled(isVSCDisabled);

        timeToleranceLabel.setEnabled(isVSCDisabled);
        timeToleranceTextField.setEnabled(isVSCDisabled);
        timeToleranceUnitLabel.setEnabled(isVSCDisabled);

        startButton.setEnabled(isVSCDisabled);
        stopButton.setEnabled(!isVSCDisabled);
    }

}
