/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.fullcourseyellow;

import base.screen.networking.data.CarInfo;
import base.screen.visualisation.LookAndFeel;
import base.screen.visualisation.gui.LPTable;
import base.screen.visualisation.gui.LPTableColumn;
import base.screen.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class CarSpeedTableModel extends TableModel {

    private int columnCount = 1;
    /**
     * Ordered list of car entries
     */
    private List<CarInfo> entries = new LinkedList<>();

    private boolean isFCYactive = false;

    @Override
    public int getRowCount() {
        return (int) Math.ceil(entries.size() / (float) columnCount);
    }

    @Override
    public LPTableColumn[] getColumns() {
        LPTableColumn[] columns = new LPTableColumn[columnCount * 3];
        for (int i = 0; i < columnCount; i++) {
            columns[i * 3] = new LPTableColumn("Driver");
            columns[i * 3 + 1] = new LPTableColumn("#")
                    .setMaxWidth(LookAndFeel.LINE_HEIGHT * 1.5f)
                    .setMinWidth(LookAndFeel.LINE_HEIGHT * 1.5f);
            columns[i * 3 + 2] = new LPTableColumn("P")
                    .setMaxWidth(LookAndFeel.LINE_HEIGHT * 3f)
                    .setMinWidth(LookAndFeel.LINE_HEIGHT * 3f);
                    //.setCellRenderer(speedRenderer);
        }
        return columns;
    }

    @Override
    public Object getValueAt(int column, int row) {
        int superColumn = (column - (column % 3)) / 3;
        int index = (superColumn) * getRowCount() + row;
        int columnIndex = column % 3;
        switch (columnIndex) {
            case 0:
                if (index >= entries.size()) {
                    return "";
                }
                CarInfo car = entries.get(index);
                String firstname = car.getDriver().getFirstName();
                String lastname = car.getDriver().getLastName();
                firstname = firstname.substring(0, Math.min(firstname.length(), 1));
                return String.format("%s. %s", firstname, lastname);
            case 1:
                if (index >= entries.size()) {
                    return "";
                }
                return String.valueOf(entries.get(index).getCarNumber());
            case 2:
                if (index >= entries.size()) {
                    return "";
                }
                return (Integer)entries.get(index).getRealtime().getPosition();
        }
        return "-";
    }

    public void setColumnCount(int count) {
        this.columnCount = count;
    }

    public void setEntries(List<CarInfo> entries) {
        this.entries = entries;
    }

    public void setFCYActive(boolean state) {
        isFCYactive = state;
    }

    private final LPTable.CellRenderer speedRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        Integer speed = (Integer) object;
        if(speed == null){
            return;
        }
        if (isFCYactive && speed > 50) {
            applet.fill(LookAndFeel.COLOR_RED);
            applet.rect(0, 0, width, height);
        }
        applet.fill(255);
        applet.textAlign(CENTER, CENTER);
        applet.text(String.valueOf(speed),
                width / 2f, height / 2f);
    };

}
