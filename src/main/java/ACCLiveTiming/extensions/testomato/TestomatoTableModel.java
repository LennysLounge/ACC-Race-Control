/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.testomato;

import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPTableColumn;
import ACCLiveTiming.visualisation.gui.TableModel;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class TestomatoTableModel extends TableModel {

    private static final Logger LOG = Logger.getLogger(TestomatoTableModel.class.getName());

    private int selectedRow = -1;

    @Override
    public int getRowCount() {
        return 15;
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("1")
            .setMinWidth(LookAndFeel.LINE_HEIGHT)
            .setMaxWidth(LookAndFeel.LINE_HEIGHT),
            new LPTableColumn("2")
            .setGrowthRate(1)
            .setMaxWidth(200),
            new LPTableColumn("3")
            .setMinWidth(LookAndFeel.LINE_HEIGHT * 1.5f)
            .setMaxWidth(LookAndFeel.LINE_HEIGHT * 1.5f),
            new LPTableColumn("4")
            .setGrowthRate(1),
            new LPTableColumn("5")
            .setGrowthRate(1)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        return String.valueOf(column) + String.valueOf(row);
    }

    @Override
    public int getSelectedRow() {
        return selectedRow;
    }

    @Override
    public void onHeaderClicked(int column) {
        LOG.info("Header column " + column + " clicked");
    }

    @Override
    public void onClick(int column, int row) {
        LOG.info("Cell " + column + row + " clicked");
        selectedRow = row;
    }

}
