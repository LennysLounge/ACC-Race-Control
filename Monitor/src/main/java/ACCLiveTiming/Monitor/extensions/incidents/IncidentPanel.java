/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions.incidents;

import ACCLiveTiming.monitor.visualisation.LookAndFeel;
import ACCLiveTiming.monitor.visualisation.gui.LPButton;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import ACCLiveTiming.monitor.visualisation.gui.LPTable;

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
