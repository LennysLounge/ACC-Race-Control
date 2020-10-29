/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.visualisation.gui.LPContainer;
import ACCLiveTiming.visualisation.gui.LPTable;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class LiveTimingPanel extends LPContainer {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(LiveTimingPanel.class.getName());

    /**
     * Reference to the extension this panel is representing.
     */
    private final LiveTimingExtension extension;
    /**
     * The table that display the live timing.
     */
    private LPTable table = new LPTable();

    public LiveTimingPanel(LiveTimingExtension extension) {
        this.extension = extension;
        setName("LIVE TIMING");

        table.setTableModel(extension.getTableModel());
        table.setOverdrawForLastLine(true);
        addComponent(table);

        /*
        table.addColumn("P", 40, false, LiveTimingEntry.positionRenderer);
        table.addColumn("Name", 240, false, LEFT, LiveTimingEntry.getName);
        table.addColumn("", 16, false, LiveTimingEntry.pitRenderer);
        table.addColumn("#", 50, false, LiveTimingEntry.carNumberRenderer);
        table.addColumn("Laps", 60, true, RIGHT, LiveTimingEntry.getLapCount);
        //table.addColumn("Gap", 80, true);
        //table.addColumn("Leader", 80, true);
        table.addColumn("Delta", 80, true, RIGHT, LiveTimingEntry.getDelta);
        table.addColumn("Lap", 80, true, RIGHT, LiveTimingEntry.getCurrentLap);
        //table.addColumn("S1", 80, true);
        //table.addColumn("S2", 80, true);
        //table.addColumn("S3", 80, true);
        table.addColumn("Last", 80, true, RIGHT, LiveTimingEntry.getLastLap);
        table.addColumn("Best", 80, true, RIGHT, LiveTimingEntry.getBestLap);
        table.addColumn("", 20, false);
        table.drawBottomRow(true);
        addComponent(table);

        table.setRowClickedAction((entry) -> {
            LOG.info(((LiveTimingEntry)entry).getCarNumber());
        });
         */
    }

    @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }

}
