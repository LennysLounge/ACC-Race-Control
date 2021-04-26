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
            new LPTableColumn("lap time"),
            new LPTableColumn("naive prediction"),
            new LPTableColumn("vMap prediction")
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
                return TimeUtils.asLapTime(car.getRealtime().getCurrentLap().getLapTimeMS());
            case 5:
                //return TimeUtils.asLapTime(getEntry(row).getNaiveLapTime());
                return TimeUtils.asLapTime(getEntry(row).getNaiveLapTime());
            case 6:
                //return TimeUtils.asLapTime(getEntry(row).getLapTime());
                return TimeUtils.asLapTime(getEntry(row).getLapTime());

        }
        return "-";
    }

}
