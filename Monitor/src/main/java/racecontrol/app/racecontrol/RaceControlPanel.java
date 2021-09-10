/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import processing.event.KeyEvent;
import static racecontrol.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPButton;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPLabel;
import racecontrol.lpgui.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class RaceControlPanel
        extends LPContainer {

    private final LPTable table;

    private final LPButton findReplayOffsetButton;
    private final LPLabel findReplayOffsetLabel;
    private final LPLabel eventListLabel;

    private boolean showFindReplayButton = false;

    private Runnable action = () -> {
    };

    public RaceControlPanel() {
        table = new LPTable();
        addComponent(table);

        findReplayOffsetButton = new LPButton("Find replay time");
        findReplayOffsetButton.setVisible(false);
        findReplayOffsetButton.setSize(200, LINE_HEIGHT);
        findReplayOffsetButton.setPosition(600, 0);
        addComponent(findReplayOffsetButton);
        findReplayOffsetLabel = new LPLabel("The replay time is currently not know. Click here to find it.");
        findReplayOffsetLabel.setVisible(false);
        findReplayOffsetLabel.setSize(600, LINE_HEIGHT);
        findReplayOffsetLabel.setPosition(20, 0);
        addComponent(findReplayOffsetLabel);

        eventListLabel = new LPLabel("Event list:");
        eventListLabel.setSize(200, LINE_HEIGHT);
        addComponent(eventListLabel);
    }

    @Override
    public void draw() {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        int headerHeight = showFindReplayButton ? LINE_HEIGHT * 2 : LINE_HEIGHT * 1;

        eventListLabel.setPosition(20, headerHeight - LINE_HEIGHT);

        table.setSize(w, h - headerHeight);
        table.setPosition(0, headerHeight);
    }

    public LPTable getTable() {
        return table;
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getKey() == 'k') {
            action.run();
        }
    }

    public void setKeyEvent(Runnable action) {
        this.action = action;
    }

    public void setShowReplayButton(boolean state) {
        if (showFindReplayButton == state) {
            return;
        }

        findReplayOffsetButton.setVisible(state);
        findReplayOffsetButton.setEnabled(state);
        findReplayOffsetLabel.setVisible(state);
        showFindReplayButton = state;
        onResize(getWidth(), getHeight());
        invalidate();
    }
    
    public LPButton getSeachReplayButton(){
        return findReplayOffsetButton;
    }

}
