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
    }

    @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }

}
