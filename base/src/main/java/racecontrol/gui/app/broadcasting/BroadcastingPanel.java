/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.broadcasting;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class BroadcastingPanel
        extends LPContainer {

    protected final LPCheckBox enableCheckBox = new LPCheckBox();
    private final LPLabel enableLabel = new LPLabel("Enable broadcasting overlay");

    public BroadcastingPanel() {
        enableCheckBox.setPosition(20, 10);
        addComponent(enableCheckBox);

        enableLabel.setPosition(50, 0);
        addComponent(enableLabel);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

}
