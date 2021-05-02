/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.livetiming;

import base.screen.extensions.livetiming.tablemodels.LiveTimingTableModel;
import base.screen.networking.data.CarInfo;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPTable;
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
    /**
     * timestamp for the last time the table was clicked.
     */
    private long lastTableClick = 0;
    /**
     * Last row that was clicked.
     */
    private int lastTableClickRow = -1;

    public LiveTimingPanel(LiveTimingExtension extension) {
        this.extension = extension;
        setName("LIVE TIMING");

        table.setTableModel(extension.getTableModel());
        table.setOverdrawForLastLine(true);
        table.setCellClickAction((c, r) -> onCellClickAction(c, r));
        addComponent(table);
    }

    @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }

    private void onCellClickAction(int column, int row) {
        //We want to change the focused car when we double click
        if (row == lastTableClickRow) {
            long now = System.currentTimeMillis();
            if (now - lastTableClick < 500) {
                //was double click
                CarInfo car = ((LiveTimingTableModel) table.getTableModel())
                        .getEntry(row)
                        .getCarInfo();
                extension.focusOnCar(car);
            }
        }
        lastTableClickRow = row;
        lastTableClick = System.currentTimeMillis();
    }

    protected void setTableModel(LiveTimingTableModel model) {
        table.setTableModel(model);
        invalidate();
    }

}
