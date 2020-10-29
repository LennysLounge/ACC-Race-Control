/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPButton;
import ACCLiveTiming.visualisation.gui.LPContainer;
import ACCLiveTiming.visualisation.gui.NewLPTable;

/**
 *
 * @author Leonard
 */
public class IncidentPanel extends LPContainer {

    private final IncidentExtension extension;
    /**
     * The table that display the incidents.
     */
    private final NewLPTable table = new NewLPTable();
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

        /*
        LPTable<Accident> t = new LPTable<>();
        t.addColumn("Nr.", 40, false, Accident.numberRenderer);
        t.addColumn("Session Time", 250, false, LEFT, Accident.getTime);
        t.addColumn("#", 50, true, Accident.carNumberRenderer);
        t.drawBottomRow(true);
        table = t;
        addComponent(table);

        showSendActionButton = SpreadSheetService.isRunning();

        if (showSendActionButton) {
            addComponent(sendEmptyActionButton);
            sendEmptyActionButton.setAction(() -> {
                extension.addEmptyAccident();
            });
        }
         */
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
