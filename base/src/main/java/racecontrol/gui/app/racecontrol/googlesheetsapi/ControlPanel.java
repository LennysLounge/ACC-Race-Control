/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class ControlPanel
        extends LPContainer {

    private final LPLabel targetHeaderLabel = new LPLabel("Target:");
    protected final LPLabel targetLabel = new LPLabel("disconnected");

    protected LPButton sendTestIncidentButton = new LPButton("send test incident");

    private final LPLabel targetsLabel = new LPLabel("Sheets:");
    private final List<LPButton> targetButtons = new ArrayList<>();

    public ControlPanel() {
        setName("Control");

        addComponent(targetHeaderLabel);
        addComponent(targetLabel);
        addComponent(sendTestIncidentButton);
        addComponent(targetsLabel);
        sendTestIncidentButton.setSize(200, LINE_HEIGHT);
    }

    private void updateComponents() {
        targetHeaderLabel.setPosition(20, 0);
        targetLabel.setPosition(120, 0);
        sendTestIncidentButton.setPosition(20, LINE_HEIGHT);
        targetsLabel.setPosition(20, LINE_HEIGHT * 2);

        int i = 3;
        for (LPButton button : targetButtons) {
            button.setPosition(20, LINE_HEIGHT * i++);
        }
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    public void addTargetButton(LPButton button) {
        button.setSize(200, LINE_HEIGHT);
        addComponent(button);
        targetButtons.add(button);
        updateComponents();
        invalidate();
    }

    public void removeTargetButtons() {
        targetButtons.forEach(b -> removeComponent(b));
        targetButtons.clear();
        updateComponents();
        invalidate();
    }

}
