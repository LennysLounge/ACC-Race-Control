/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.test;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import processing.core.PFont;
import racecontrol.gui.CustomPApplet;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.PageController;
import racecontrol.gui.app.statuspanel.StatusPanelManager;
import racecontrol.gui.lpui.LPAnimationTask;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPCollapsablePanel;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class TestPanel
        extends LPContainer
        implements PageController {

    private final TestStatusPanel testStatusPanel = new TestStatusPanel();

    private final LPAnimationTask testAnimation = new LPAnimationTask(this::animationFunction, 1000);

    private final LPButton animationButton;

    private final LPCollapsablePanel collapsePanel;
    private final LPLabel collapsePanelLable = new LPLabel("Hello");

    private final Menu.MenuItem menuItem;

    public TestPanel() {
        setName("Debug");

        this.menuItem = new Menu.MenuItem("Debug",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Debugging.png"));

        LPButton addStatusPanel = new LPButton("Add status panel");
        addStatusPanel.setSize(200, LINE_HEIGHT);
        addStatusPanel.setPosition(20, 0);
        addStatusPanel.setAction(() -> {
            TestStatusPanel panel = new TestStatusPanel();
            panel.closeButton.setAction(() -> {
                StatusPanelManager.getInstance().removeStatusPanel(panel);
            });
            StatusPanelManager.getInstance().addStatusPanel(panel);
        });
        addComponent(addStatusPanel);

        LPButton removeStatusPanel = new LPButton("Remove status panel");
        removeStatusPanel.setSize(200, LINE_HEIGHT);
        removeStatusPanel.setPosition(240, 0);
        removeStatusPanel.setAction(() -> {
            StatusPanelManager.getInstance().removeStatusPanel(testStatusPanel);
        });
        addComponent(removeStatusPanel);

        animationButton = new LPButton("Animate");
        animationButton.setSize(100, LINE_HEIGHT);
        animationButton.setPosition(460, 0);
        animationButton.setAction(() -> {
            testAnimation.restart();
        });
        addComponent(animationButton);
        
        LPButton crashButton = new LPButton("crash");
        crashButton.setSize(100, LINE_HEIGHT);
        crashButton.setPosition(680, 0);
        crashButton.setAction(() -> {
            Object o = null;
            o.toString();
        });
        addComponent(crashButton);

        addAnimationTask(testAnimation);

        collapsePanel = new LPCollapsablePanel("Collapse");
        collapsePanel.setSize(200, 200);
        collapsePanel.setPosition(10, 600);
        BluePanel panel = new BluePanel();
        collapsePanel.addComponent(panel);
        panel.setSize(300, 200 - LINE_HEIGHT);
        panel.setPosition(0, LINE_HEIGHT);
        collapsePanel.setCollapsed(true);
        addComponent(collapsePanel);
        addComponent(collapsePanelLable);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 5; i++) {
                if ((j * 11 + i) % 2 == 0) {
                    applet.textFont(LookAndFeel.fontMedium());
                } else {
                    applet.textFont(LookAndFeel.fontRegular());
                }
                applet.textSize(10 + j + 3);
                applet.fill(COLOR_WHITE);
                applet.textAlign(CENTER, CENTER);
                applet.noStroke();
                applet.text("HELLO", 1000 + 100 * i, 80 + 30 * j);
            }
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.textSize(TEXT_SIZE);
        applet.textFont(LookAndFeel.fontRegular());
        applet.textSize(TEXT_SIZE);

        PFont regular = LookAndFeel.fontRegular();
        PFont medium = LookAndFeel.fontMedium();

        int i = 0;
        applet.text("regular: " + regular.toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular: " + regular.getName(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular native: " + regular.getNative().toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular native: " + regular.getNative().hashCode(), 10, 60 + LINE_HEIGHT * i++);

        /*
        i++;
        
        applet.text("medium: " + medium.toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("medium: " + medium.getName(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("medium native: " + medium.getNative().toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("medium native: " + medium.getNative().hashCode(), 10, 60 + LINE_HEIGHT * i++);
         */
        i++;
        applet.textSize(21);

        applet.text("regular: " + regular.toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular: " + regular.getName(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular native: " + regular.getNative().toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular native: " + regular.getNative().hashCode(), 10, 60 + LINE_HEIGHT * i++);
        i++;
        applet.textSize(20);

        applet.text("regular: " + regular.toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular: " + regular.getName(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular native: " + regular.getNative().toString(), 10, 60 + LINE_HEIGHT * i++);
        applet.text("regular native: " + regular.getNative().hashCode(), 10, 60 + LINE_HEIGHT * i++);

        updateComponents();
    }

    private void animationFunction(LPAnimationTask task, int dt) {
        int x = 460 + (int) (100 * testAnimation.getProgressNormal());
        animationButton.setPosition(x, 0);
        invalidate();
    }

    private void updateComponents() {
        collapsePanelLable.setPosition(10, collapsePanel.getPosY() + collapsePanel.getHeight());
    }

    @Override
    public LPContainer getPanel() {
        return this;
    }

    @Override
    public Menu.MenuItem getMenuItem() {
        return menuItem;
    }

}
