/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.test;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import processing.core.PFont;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.app.statuspanel.StatusPanelManager;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class TestPanel extends LPContainer {

    private final TestStatusPanel testStatusPanel = new TestStatusPanel();

    public TestPanel() {
        setName("Debug");

        testStatusPanel.closeButton.setAction(() -> {
            StatusPanelManager.getInstance().removeStatusPanel(testStatusPanel);
        });

        LPButton addStatusPanel = new LPButton("Add status panel");
        addStatusPanel.setSize(200, LINE_HEIGHT);
        addStatusPanel.setPosition(20, 0);
        addStatusPanel.setAction(() -> {
            StatusPanelManager.getInstance().addStatusPanel(testStatusPanel);
        });
        addComponent(addStatusPanel);

        LPButton removeStatusPanel = new LPButton("Remove status panel");
        removeStatusPanel.setSize(200, LINE_HEIGHT);
        removeStatusPanel.setPosition(240, 0);
        removeStatusPanel.setAction(() -> {
            StatusPanelManager.getInstance().removeStatusPanel(testStatusPanel);
        });
        addComponent(removeStatusPanel);

    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        for (int j = 0; j < 11; j++) {
            for (int i = 0; i < 11; i++) {
                if ((j * 11 + i) % 2 == 0) {
                    applet.textFont(LookAndFeel.fontMedium());
                } else {
                    applet.textFont(LookAndFeel.fontRegular());
                }
                applet.fill(COLOR_WHITE);
                applet.textAlign(CENTER, CENTER);
                applet.noStroke();
                applet.text("HELLO", 100 + 100 * i, 400 + 30 * j);
            }
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());

        PFont regular = LookAndFeel.fontRegular();
        PFont medium = LookAndFeel.fontMedium();

        applet.text("regular: " + regular.toString(), 10, 60);
        applet.text("regular: " + regular.getName(), 10, 100);
        applet.text("regular: " + regular.getPostScriptName(), 10, 140);
        
        applet.text("medium: " + medium.toString(), 10, 220);
        applet.text("medium: " + medium.getName(), 10, 260);
        applet.text("medium: " + medium.getPostScriptName(), 10, 300);
    }

}
