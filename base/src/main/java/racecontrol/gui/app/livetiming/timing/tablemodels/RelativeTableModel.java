/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.model.Car;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_ORANGE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.BestLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CurrentLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LapCount;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LastLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.OvertakeIndicator;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class RelativeTableModel
        extends LiveTimingTableModel {

    private Car selectedCar = null;

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn()
            .setCellRenderer(this::custonNameRenderer),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new OvertakeIndicator(),
            new LPTableColumn("Gap")
            .setMinWidth(100)
            .setMaxWidth(100)
            .setTextAlign(RIGHT)
            .setCellRenderer(this::gapToNextRenderer),
            new LPTableColumn("Total")
            .setMinWidth(100)
            .setMaxWidth(150)
            .setTextAlign(RIGHT)
            .setCellRenderer(this::totalGapRenderer),
            new CurrentLaptime(),
            new LastLaptime(),
            new BestLaptime(),
            new LapCount()
        };
    }

    @Override
    public String getName() {
        return "Relative";
    }

    @Override
    public void sort() {
        if (entries.isEmpty()) {
            return;
        }
        selectedCar = entries.get(0);
        if (getSelectedRow() > 0 && getSelectedRow() < entries.size()) {
            selectedCar = entries.get(getSelectedRow());
        }
        final Car pivot = selectedCar;
        entries = entries.stream()
                .sorted((c1, c2) -> {
                    float c1dif = pivot.splinePosition - c1.splinePosition;
                    c1dif -= (Math.abs(c1dif) > 0.5) ? Math.signum(c1dif) : 0;
                    float c2dif = pivot.splinePosition - c2.splinePosition;
                    c2dif -= (Math.abs(c2dif) > 0.5) ? Math.signum(c2dif) : 0;
                    return Float.compare(c1dif, c2dif);
                })
                .collect(toList());
    }

    private void custonNameRenderer(PApplet applet,
            LPTable.RenderContext context) {
        boolean isLapped = false;
        boolean isLapping = false;
        if (selectedCar != null) {
            Car car = (Car) context.object;
            float distance = car.raceDistance
                    - selectedCar.raceDistance;
            isLapped = distance < -0.5;
            isLapping = distance > 0.5;
        }
        NameColumn.nameRenderer(applet, context, isLapped, isLapping);
    }

    private void gapToNextRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);

        int gap = 0;
        String text = "--";
        if (context.rowIndex < getSelectedRow()) {
            gap = car.gapBehind;
            text = TimeUtils.asGap(gap);
        } else if (context.rowIndex > getSelectedRow()) {
            gap = car.gapAhead;
            text = TimeUtils.asGap(-gap);
        }

        applet.fill(COLOR_WHITE);
        if (gap < 1000 && gap > 0) {
            applet.fill(COLOR_ORANGE);
        }

        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width - 20, context.height / 2);
    }

    private void totalGapRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);

        String text = "--";
        if (context.rowIndex < getSelectedRow()) {
            int gap = 0;
            for (int i = getSelectedRow() - 1; i >= context.rowIndex; i--) {
                gap += getEntry(i).gapBehind;
            }
            text = TimeUtils.asGap(gap);
        } else if (context.rowIndex > getSelectedRow()) {
            int gap = 0;
            for (int i = getSelectedRow() + 1; i <= context.rowIndex; i++) {
                gap += getEntry(i).gapAhead;
            }
            text = TimeUtils.asGap(-gap);
        }

        applet.fill(COLOR_WHITE);
        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width - 20, context.height / 2);

    }
}
