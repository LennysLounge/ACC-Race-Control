/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPCollapsablePanel;
import racecontrol.gui.lpui.LPComponent;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class LiveTimingPanel
        extends LPContainer {

    protected final LPButton detachLiveTimingButton = new LPButton("Detach");
    private final LPLabel viewLabel = new LPLabel("View Qualifying");
    protected final LPPaginatorButton viewLeftButton = new LPPaginatorButton(true);
    protected final LPPaginatorButton viewRightButton = new LPPaginatorButton(false);
    /**
     * Collapsable panel that holds the broadcasting controls.
     */
    private final LPCollapsablePanel collapsablePanel = new LPCollapsablePanel("Broadcasting control");
    /**
     * The live timing table.
     */
    private LPComponent liveTimingTable;
    /**
     * Indicates that the live timing table is visible.
     */
    private boolean isLiveTimingTableVisible = true;
    /**
     * Panel that holds the broadcasting controls.
     */
    private final LPContainer broadcastingControls;

    public LiveTimingPanel(LPContainer broadcastingControls) {
        this.broadcastingControls = broadcastingControls;

        detachLiveTimingButton.setPosition(10, 0);
        detachLiveTimingButton.setSize(80, LINE_HEIGHT);
        detachLiveTimingButton.setBackgroundColor(COLOR_DARK_GRAY);
        addComponent(detachLiveTimingButton);

        addComponent(viewLabel);
        viewLeftButton.setSize(LINE_HEIGHT, LINE_HEIGHT);
        addComponent(viewLeftButton);
        viewRightButton.setSize(LINE_HEIGHT, LINE_HEIGHT);
        addComponent(viewRightButton);

        collapsablePanel.setAction(() -> {
            onResize((int) getWidth(), (int) getHeight());
            invalidate();
        });
        collapsablePanel.addComponent(broadcastingControls);
        addComponent(collapsablePanel);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        /*
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text("View Qualifying", getWidth() / 2f, LINE_HEIGHT / 2f);

        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text("Detach", getWidth() - 10, LINE_HEIGHT / 2f);

        applet.stroke(COLOR_WHITE);
        applet.strokeWeight(2);
        int x = (int) (getWidth() / 2f - 300);
        applet.line(x, LINE_HEIGHT / 2f, x + 10, LINE_HEIGHT / 2f - 10);
        applet.line(x, LINE_HEIGHT / 2f, x + 10, LINE_HEIGHT / 2f + 10);

        x = (int) (getWidth() / 2f + 300);
        applet.line(x, LINE_HEIGHT / 2f, x - 10, LINE_HEIGHT / 2f - 10);
        applet.line(x, LINE_HEIGHT / 2f, x - 10, LINE_HEIGHT / 2f + 10);

        applet.strokeWeight(1);
         */
    }

    @Override
    public void onResize(float w, float h) {
        viewLabel.setPosition((w - viewLabel.getWidth()) / 2f, 0);
        int distance = (int) Math.max(100, w * 0.2f);
        viewLeftButton.setPosition(w / 2f - distance - viewLeftButton.getWidth(), 0);
        viewRightButton.setPosition(w / 2f + distance, 0);

        float broadcastControllHeight = LINE_HEIGHT;
        if (!collapsablePanel.isCollapsed()) {
            if (isLiveTimingTableVisible) {
                broadcastControllHeight = (LINE_HEIGHT * 7.5f);
            } else {
                broadcastControllHeight = h - LINE_HEIGHT;
            }
        }
        broadcastingControls.setPosition(0, LINE_HEIGHT);
        broadcastingControls.setSize(w, LINE_HEIGHT * 6.5f);

        collapsablePanel.setSize(w, broadcastControllHeight);
        collapsablePanel.setPosition(0, h - broadcastControllHeight);

        if (isLiveTimingTableVisible) {
            liveTimingTable.setSize(w, h - LINE_HEIGHT - broadcastControllHeight);
            liveTimingTable.setPosition(0, LINE_HEIGHT);
        }
        detachLiveTimingButton.setPosition(w - 90, 0);

    }

    public void addLiveTimingTable(LPComponent table) {
        this.liveTimingTable = table;
        addComponent(table);
        isLiveTimingTableVisible = true;
        onResize(getWidth(), getHeight());
        invalidate();
    }

    public void removeLiveTimingTable(LPComponent table) {
        removeComponent(table);
        this.liveTimingTable = null;
        isLiveTimingTableVisible = false;
        onResize(getWidth(), getHeight());
    }

}
