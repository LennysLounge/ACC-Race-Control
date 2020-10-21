/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.testomato;

import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.Column;
import ACCLiveTiming.visualisation.gui.TableModel;

/**
 *
 * @author Leonard
 */
public class TestomatoTableModel extends TableModel {

    @Override
    public int getRowCount() {
        return 5;
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
            new Column("1")
                .setMinWidth(LookAndFeel.LINE_HEIGHT)
                .setMaxWidth(LookAndFeel.LINE_HEIGHT),
            new Column("2")
                .setGrowthRate(1),
            new Column("3")
                .setMinWidth(LookAndFeel.LINE_HEIGHT*1.5f)
                .setMaxWidth(LookAndFeel.LINE_HEIGHT*1.5f),
            new Column("4")
                .setGrowthRate(2),
            new Column("5")
                .setGrowthRate(5)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        return String.valueOf(column) + String.valueOf(row);
    }

}
