/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.debug;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class DebugConfigPanel
        extends LPContainer {

    private final LPCheckBox enabledCheckBox;
    private final LPLabel enabledLabel;

    public DebugConfigPanel() {
        setName("Debug");

        enabledCheckBox = new LPCheckBox();
        enabledCheckBox.setPosition(20, (LINE_HEIGHT - TEXT_SIZE) / 2);
        addComponent(enabledCheckBox);
        enabledLabel = new LPLabel("Enable");
        enabledLabel.setPosition(60, 0);
        addComponent(enabledLabel);

    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    public boolean isExtensionEnabled() {
        return enabledCheckBox.isSelected();
    }

}
