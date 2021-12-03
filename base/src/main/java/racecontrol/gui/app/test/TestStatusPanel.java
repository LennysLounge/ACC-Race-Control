/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import processing.core.PApplet;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
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
    private int backgroundColor;

    public TestStatusPanel() {
        message.setPosition(20, 0);
        addComponent(message);

        closeButton.setSize(200, LINE_HEIGHT);
        closeButton.setPosition(400, 0);
        addComponent(closeButton);

        List<Integer> colors = Arrays.asList(
                LookAndFeel.COLOR_BLUE,
                LookAndFeel.COLOR_GREEN,
                LookAndFeel.COLOR_GT4,
                LookAndFeel.COLOR_ORANGE,
                LookAndFeel.COLOR_PORSCHE_CUP,
                LookAndFeel.COLOR_PRACTICE,
                LookAndFeel.COLOR_PURPLE,
                LookAndFeel.COLOR_QUALIFYING,
                LookAndFeel.COLOR_RACE,
                LookAndFeel.COLOR_RED,
                LookAndFeel.COLOR_SUPER_TROFEO,
                LookAndFeel.COLOR_DARK_DARK_GRAY
        );
        Random rand = new Random();
        backgroundColor = colors.get(rand.nextInt(colors.size()));
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(backgroundColor);
        applet.rect(0, 0, getWidth(), getHeight());
    }

}
