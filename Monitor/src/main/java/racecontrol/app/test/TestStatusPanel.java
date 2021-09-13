/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.test;

import processing.core.PApplet;
import static racecontrol.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPButton;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPLabel;

/**
 *
 * @author Leonard
 */
public class TestStatusPanel extends LPContainer {

    protected final LPButton closeButton = new LPButton("Close");
    private final LPLabel message = new LPLabel("This is a test status message");

    public TestStatusPanel() {
        message.setPosition(20, 0);
        addComponent(message);

        closeButton.setSize(200, LINE_HEIGHT);
        closeButton.setPosition(400, 0);
        addComponent(closeButton);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

}
