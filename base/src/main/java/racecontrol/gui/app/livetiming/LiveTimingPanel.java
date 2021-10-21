/*
 * Copyright (c) 2021 Leonard Schüngel
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
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.BROADCASTING_CONTROLS_COLLAPSED;

/**
 *
 * @author Leonard
 */
public class LiveTimingPanel
        extends LPContainer {

    protected final LPButton detachLiveTimingButton = new LPButton("Detach");
    protected final LPLabel viewLabel = new LPLabel("");
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
        setName("Live Timing");
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
            PersistantConfig.put(BROADCASTING_CONTROLS_COLLAPSED, collapsablePanel.isCollapsed());
            onResize((int) getWidth(), (int) getHeight());
            invalidate();
        });
        collapsablePanel.addComponent(broadcastingControls);
        addComponent(collapsablePanel);
        collapsablePanel.setCollapsed(PersistantConfig.get(BROADCASTING_CONTROLS_COLLAPSED));
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        updateComponents();
    }

    @Override
    public void onResize(float w, float h) {
        // live timing view selection.
        viewLabel.setPosition((w - viewLabel.getWidth()) / 2f, 0);
        int distance = (int) Math.max(100, w * 0.2f);
        viewLeftButton.setPosition((int) (w / 2f - distance - viewLeftButton.getWidth()), 0);
        viewRightButton.setPosition((int) (w / 2f + distance), 0);

        detachLiveTimingButton.setPosition(w - 90, 0);

        broadcastingControls.setPosition(0, LINE_HEIGHT);
        broadcastingControls.setSize(w, LINE_HEIGHT * 6.5f);

        collapsablePanel.setSize(w, LINE_HEIGHT * 7.5f);

        updateComponents();
    }

    private void updateComponents() {
        float w = getWidth();
        float h = getHeight();

        collapsablePanel.setPosition(0, h - collapsablePanel.getHeight());
        if (isLiveTimingTableVisible) {
            liveTimingTable.setSize(w - 20, h - LINE_HEIGHT - collapsablePanel.getHeight());
            liveTimingTable.setPosition(10, LINE_HEIGHT);
        }
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
