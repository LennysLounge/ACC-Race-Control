/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.networking.data.CarInfo;
import static ACCLiveTiming.networking.enums.DriverCategory.BRONZE;
import static ACCLiveTiming.networking.enums.DriverCategory.GOLD;
import static ACCLiveTiming.networking.enums.DriverCategory.PLATINUM;
import static ACCLiveTiming.networking.enums.DriverCategory.SILVER;
import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPTableColumn;
import ACCLiveTiming.visualisation.gui.NewLPTable;
import ACCLiveTiming.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class LiveTimingTableModel extends TableModel {

    private final NewLPTable.CellRenderer positionRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        Tuple t = (Tuple) object;
        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if ((boolean) t.right) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(0, 0, width, height);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.text(String.valueOf(t.left),
                width / 2f, height / 2f);
    };

    private final NewLPTable.CellRenderer pitRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        boolean isInPits = (boolean) object;
        if (isInPits) {
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_WHITE);
            applet.rect(0, 0, width, height);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(12);
            applet.text("P", width / 2f, height / 2f);
            applet.textSize(LookAndFeel.TEXT_SIZE);
        }
    };

    private final NewLPTable.CellRenderer carNumberRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = (CarInfo) object;
        int backColor = 0;
        int frontColor = 0;
        switch (car.getDriver().getCategory()) {
            case BRONZE:
                backColor = LookAndFeel.COLOR_RED;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
            case SILVER:
                backColor = LookAndFeel.COLOR_GRAY;
                frontColor = LookAndFeel.COLOR_WHITE;
                break;
            case GOLD:
            case PLATINUM:
                backColor = LookAndFeel.COLOR_WHITE;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
        }
        applet.noStroke();
        applet.fill(backColor);
        applet.rect(0, 0, width, height);
        applet.fill(frontColor);
        applet.textAlign(CENTER, CENTER);
        applet.text(car.getCarNumber(), width / 2f, height / 2f);
    };

    private List<LiveTimingEntry> entries = new LinkedList<>();

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("P")
            .setMinWidth(40)
            .setMaxWidth(40)
            .setCellRenderer(positionRenderer),
            new LPTableColumn("Name")
            .setMaxWidth(240),
            new LPTableColumn("")
            .setMaxWidth(16)
            .setMinWidth(16)
            .setCellRenderer(pitRenderer),
            new LPTableColumn("#")
            .setMinWidth(60)
            .setMaxWidth(60)
            .setCellRenderer(carNumberRenderer),
            new LPTableColumn("lap")
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        LiveTimingEntry entry = entries.get(row);
        switch (column) {
            case 0:
                return new Tuple(
                        entry.getCarInfo().getRealtime().getPosition(),
                        entry.isFocused()
                );
            case 1:
                return entry.getName();
            case 2:
                return entry.isInPits();
            case 3:
                return entry.getCarInfo();
            case 4:
                return "--:--.--";
        }
        return "-";
    }

    public void setEntries(List<LiveTimingEntry> entries) {
        this.entries = entries;
    }

    private class Tuple {

        public Object left;
        public Object right;

        public Tuple(Object left, Object right) {
            this.left = left;
            this.right = right;
        }
    }

}
