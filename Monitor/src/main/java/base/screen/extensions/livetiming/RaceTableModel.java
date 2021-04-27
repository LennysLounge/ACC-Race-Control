/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.screen.networking.data.CarInfo;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.gui.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class RaceTableModel
        extends QualifyingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Interval"),
            new LPTableColumn("To Leader")
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        CarInfo car = getEntry(row).getCarInfo();

        switch (column) {
            case 0:
            case 1:
            case 2:
            case 3:
                return getEntry(row);
            case 4:
                return TimeUtils.asDelta(getEntry(row).getGap());
            case 5:
                if (getEntry(row).showLapsBehind()) {
                    return String.format("+%d Laps", getEntry(row).getLapsBehind());
                } else {
                    return TimeUtils.asDelta(getEntry(row).getTotal());
                }

        }
        return "-";
    }

}
