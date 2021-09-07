/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.debug;

import static racecontrol.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import static racecontrol.LookAndFeel.TEXT_SIZE;
import racecontrol.lpgui.gui.LPCheckBox;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPLabel;

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
    public void draw() {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    public boolean isExtensionEnabled() {
        return enabledCheckBox.isSelected();
    }

}
