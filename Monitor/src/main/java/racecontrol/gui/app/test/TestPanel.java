/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.test;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.app.AppController;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class TestPanel extends LPContainer {
    
    private final TestStatusPanel testStatusPanel = new TestStatusPanel();

    public TestPanel() {
        AppController appController = AppController.getInstance();
        
        
        testStatusPanel.closeButton.setAction(()->{
            appController.removeStatusPanel(testStatusPanel);
        });
        
        LPButton addStatusPanel = new LPButton("Add status panel");
        addStatusPanel.setSize(200, LINE_HEIGHT);
        addStatusPanel.setPosition(20, 0);
        addStatusPanel.setAction(()->{
            appController.addStatusPanel(testStatusPanel);
        });
        addComponent(addStatusPanel);
        
        LPButton removeStatusPanel = new LPButton("Remove status panel");
        removeStatusPanel.setSize(200, LINE_HEIGHT);
        removeStatusPanel.setPosition(240, 0);
        removeStatusPanel.setAction(()->{
            appController.removeStatusPanel(testStatusPanel);
        });
        addComponent(removeStatusPanel);
        
        
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.fill(255);
        applet.text("Hello World", 200, 200);
    }

}
