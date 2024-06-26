/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.autobroadcast;

import processing.core.PApplet;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.table.LPTable;

/**
 *
 * @author Leonard
 */
public class AutobroadcastPanel
        extends LPContainer {

    protected final LPCheckBox enableCheckBox = new LPCheckBox();
    private final LPLabel enableLabel = new LPLabel("Enable autopilot");

    protected final LPCheckBox sortByRatingCheckBox = new LPCheckBox();
    private final LPLabel sortByRatingLabel = new LPLabel("Sort by rating");
    protected final LPCheckBox showCameraRatingsCheckBox = new LPCheckBox();
    private final LPLabel showCameraRatingsLabel = new LPLabel("Show camera ratings");
    protected final LPTable ratingTable = new LPTable();

    protected final LPLabel currentCamera = new LPLabel("Current Camera:");
    protected final LPLabel screenTimeLabel = new LPLabel("Total on time: ");

    public AutobroadcastPanel() {
        enableCheckBox.setPosition(20, 10);
        addComponent(enableCheckBox);
        enableLabel.setPosition(50, 0);
        addComponent(enableLabel);

        addComponent(sortByRatingCheckBox);
        addComponent(sortByRatingLabel);
        addComponent(showCameraRatingsCheckBox);
        addComponent(showCameraRatingsLabel);
        addComponent(ratingTable);

        addComponent(currentCamera);
        currentCamera.setPosition(20, LINE_HEIGHT);
        currentCamera.setSize(500, LINE_HEIGHT);

        addComponent(screenTimeLabel);
        screenTimeLabel.setPosition(20, LINE_HEIGHT * 2);
        screenTimeLabel.setSize(500, LINE_HEIGHT);

    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        ratingTable.setPosition(10, LINE_HEIGHT * 4);
        ratingTable.setSize(w - 20, h - LINE_HEIGHT * 4);

        sortByRatingCheckBox.setPosition(20, LINE_HEIGHT * 3
                + (LINE_HEIGHT - TEXT_SIZE) / 2);
        sortByRatingLabel.setPosition(50, LINE_HEIGHT * 3);
        showCameraRatingsCheckBox.setPosition(200, LINE_HEIGHT * 3
                + (LINE_HEIGHT - TEXT_SIZE) / 2);
        showCameraRatingsLabel.setPosition(230, LINE_HEIGHT * 3);
    }

}
