/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol;

import processing.core.PApplet;
import processing.event.KeyEvent;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.table.LPTable;

/**
 *
 * @author Leonard
 */
public class RaceControlPanel
        extends LPContainer {

    private final LPTable table;

    private final LPButton findReplayOffsetButton;
    private final LPLabel findReplayOffsetLabel;

    private final LPLabel subModuleLabel;
    private final LPLabel eventListLabel;

    protected final LPButton exportButton;
    protected final LPButton googleSheetsButton;
    protected final LPButton virtualSafetyCarButton;
    protected final LPButton placeHolder2;
    protected final LPButton placeHolder3;

    private boolean showFindReplayButton = false;

    private Runnable createDummyIncidents = () -> {
    };

    public RaceControlPanel() {
        setName("Race Control");
        table = new LPTable();
        addComponent(table);

        findReplayOffsetButton = new LPButton("Find replay time");
        findReplayOffsetButton.setVisible(false);
        findReplayOffsetButton.setSize(200, LINE_HEIGHT);
        findReplayOffsetButton.setPosition(600, 0);
        addComponent(findReplayOffsetButton);
        findReplayOffsetLabel = new LPLabel("The replay time is currently not know. Click here to find it.");
        findReplayOffsetLabel.setVisible(false);
        findReplayOffsetLabel.setPosition(20, 0);
        addComponent(findReplayOffsetLabel);

        eventListLabel = new LPLabel("Event list:");
        eventListLabel.setSize(200, LINE_HEIGHT);
        addComponent(eventListLabel);

        subModuleLabel = new LPLabel("Sub modules:");
        subModuleLabel.setSize(200, LINE_HEIGHT);
        subModuleLabel.setPosition(20, 0);
        //addComponent(subModuleLabel);

        exportButton = new LPButton("Export event list");
        //addComponent(exportButton);
        googleSheetsButton = new LPButton("Google Sheets API");
        addComponent(googleSheetsButton);
        virtualSafetyCarButton = new LPButton("Virtual safety car");
        addComponent(virtualSafetyCarButton);
        placeHolder2 = new LPButton("placeHolder2");
        //addComponent(placeHolder2);
        placeHolder3 = new LPButton("placeHolder3");
        //addComponent(placeHolder3);

    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        float heightOffset = showFindReplayButton ? LINE_HEIGHT : 0;

        float buttonWidth = Math.min(400, w / 2);
        float buttonHeight = LINE_HEIGHT * 1.5f;
        float buttonPadding = LINE_HEIGHT * 0.1f;

        googleSheetsButton.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        googleSheetsButton.setPosition(buttonPadding * 2, heightOffset + buttonPadding * 2);
        virtualSafetyCarButton.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        virtualSafetyCarButton.setPosition(buttonWidth + buttonPadding, heightOffset + buttonPadding * 2);
        placeHolder2.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        placeHolder2.setPosition(buttonPadding * 2, heightOffset + buttonHeight + buttonPadding);
        placeHolder3.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        placeHolder3.setPosition(buttonWidth + buttonPadding, heightOffset + buttonHeight + buttonPadding);

        float headerHeight = heightOffset + buttonHeight * 2 + LINE_HEIGHT;
        eventListLabel.setPosition(20, headerHeight - LINE_HEIGHT);
        table.setSize(w - 20, h - headerHeight - 10);
        table.setPosition(10, headerHeight);
    }

    public LPTable getTable() {
        return table;
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getKey() == 'k') {
            createDummyIncidents.run();
        }
    }

    public void setKeyEvent(Runnable action) {
        this.createDummyIncidents = action;
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

    public LPButton getSeachReplayButton() {
        return findReplayOffsetButton;
    }

    public LPButton googleSheetsModuleButton() {
        return googleSheetsButton;
    }

}
