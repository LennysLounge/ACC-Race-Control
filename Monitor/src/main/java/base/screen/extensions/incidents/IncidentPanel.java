/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.incidents;

import base.screen.visualisation.LookAndFeel;
import base.screen.visualisation.gui.LPButton;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPTable;

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
     * Button to send an empty accident.
     */
    private final LPButton sendEmptyActionButton = new LPButton("Send empty incident");
    /**
     * Indicates it the sendEmptyActionButton is visible.
     */
    private final boolean showSendActionButton = false;

    public IncidentPanel(IncidentExtension extension) {
        this.extension = extension;
        setName("INCIDENTS");

        table.setOverdrawForLastLine(true);
        table.setTableModel(extension.getTableModel());
        addComponent(table);
    }

    @Override
    public void onResize(int w, int h) {
        float height = LookAndFeel.LINE_HEIGHT;
        if (!showSendActionButton) {
            height = 0;
        }
        sendEmptyActionButton.setPosition(height * 0.1f, height * 0.1f);
        sendEmptyActionButton.setSize(300, height * 0.8f);
        table.setPosition(0, height);
        table.setSize(w, h - height);
    }

}
