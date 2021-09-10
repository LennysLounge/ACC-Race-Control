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

    private final LPLabel subModuleLabel;
    private final LPLabel eventListLabel;

    private final LPButton googleSheetsButton;
    private final LPButton placeHolder1;
    private final LPButton placeHolder2;
    private final LPButton placeHolder3;

    private boolean showFindReplayButton = false;

    private Runnable createDummyIncidents = () -> {
    };

    public RaceControlPanel() {
        table = new LPTable();
        addComponent(table);

        findReplayOffsetButton = new LPButton("Find replay time");
        findReplayOffsetButton.setVisible(false);
        findReplayOffsetButton.setSize(200, LINE_HEIGHT);
        findReplayOffsetButton.setPosition(600, 0);
        //addComponent(findReplayOffsetButton);
        findReplayOffsetLabel = new LPLabel("The replay time is currently not know. Click here to find it.");
        findReplayOffsetLabel.setVisible(false);
        findReplayOffsetLabel.setSize(600, LINE_HEIGHT);
        findReplayOffsetLabel.setPosition(20, 0);
        //addComponent(findReplayOffsetLabel);

        eventListLabel = new LPLabel("Event list:");
        eventListLabel.setSize(200, LINE_HEIGHT);
        addComponent(eventListLabel);

        subModuleLabel = new LPLabel("Sub modules:");
        subModuleLabel.setSize(200, LINE_HEIGHT);
        subModuleLabel.setPosition(20, 0);
        //addComponent(subModuleLabel);

        googleSheetsButton = new LPButton("Google Sheets API");
        addComponent(googleSheetsButton);
        placeHolder1 = new LPButton("placeHolder1");
        //addComponent(placeHolder1);
        placeHolder2 = new LPButton("placeHolder2");
        //addComponent(placeHolder2);
        placeHolder3 = new LPButton("placeHolder3");
        //addComponent(placeHolder3);

    }

    @Override
    public void draw() {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        float buttonWidth = Math.min(400, w / 2);
        float buttonHeight = LINE_HEIGHT * 1.5f;
        float buttonPadding = LINE_HEIGHT * 0.1f;

        googleSheetsButton.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        googleSheetsButton.setPosition(buttonPadding * 2, buttonPadding * 2);
        placeHolder1.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        placeHolder1.setPosition(buttonWidth + buttonPadding, buttonPadding * 2);
        placeHolder2.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        placeHolder2.setPosition(buttonPadding * 2, buttonHeight + buttonPadding);
        placeHolder3.setSize(buttonWidth - buttonPadding * 3, buttonHeight - buttonPadding * 3);
        placeHolder3.setPosition(buttonWidth + buttonPadding, buttonHeight + buttonPadding);

        float headerHeight = buttonHeight + LINE_HEIGHT;
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
    
    public LPButton googleSheetsModuleButton(){
        return googleSheetsButton;
    }

}
