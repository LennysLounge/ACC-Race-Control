/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.test;

import processing.core.PApplet;
import racecontrol.gui.CustomPApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class TestStatusPanel
        extends LPContainer {

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
