/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents;

import processing.event.KeyEvent;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import racecontrol.visualisation.gui.LPButton;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPLabel;
import racecontrol.visualisation.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class IncidentPanel extends LPContainer {

    private final IncidentExtension extension;
    /**
     * The table that display the incidents.
     */
    private final LPTable table = new LPTable();
    /**
     * Label shows the current replay time.
     */
    private final LPLabel replayOffsetLabel = new LPLabel("Searching for replay offset...");
    /**
     * Button to search for the replay time.
     */
    private final LPButton findReplayOffsetButton = new LPButton("Find replay offset");

    private boolean showFirstLine = true;

    public IncidentPanel(IncidentExtension extension) {
        this.extension = extension;
        setName("INCIDENTS");

        table.setTableModel(extension.getTableModel());
        addComponent(table);
        replayOffsetLabel.setPosition(20, 0);
        replayOffsetLabel.setSize(280, LINE_HEIGHT);
        addComponent(replayOffsetLabel);
        findReplayOffsetButton.setPosition(400, 0);
        findReplayOffsetButton.setSize(200, LINE_HEIGHT);
        findReplayOffsetButton.setVisible(false);
        addComponent(findReplayOffsetButton);
        findReplayOffsetButton.setAction(() -> {
            extension.findReplayOffset();
            findReplayOffsetButton.setEnabled(false);
        });
    }

    @Override
    public void onResize(int w, int h) {
        int y = (showFirstLine) ? LINE_HEIGHT : 0;
        table.setPosition(0, y);
        table.setSize(w, h - y);
    }
    
    @Override
    public void onKeyPressed(KeyEvent event) {
        if(event.getKey() == 'k'){
            extension.createDummyIncident();
        }
    }

    public void setReplayOffsetKnown() {
        findReplayOffsetButton.setVisible(false);
        replayOffsetLabel.setVisible(false);
        showFirstLine = false;
        onResize((int) getWidth(), (int) getHeight());
        invalidate();
    }

    public void enableSearchButton() {
        findReplayOffsetButton.setVisible(true);
        replayOffsetLabel.setText("Replay offset is unknown, search required.");
        invalidate();
    }
}
