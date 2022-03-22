/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_THREE_CALC;
import static racecontrol.client.extension.statistics.CarProperties.OVERTAKE_INDICATOR;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT_ACCURATE;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME_STATIONARY;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_COMPLEX;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SPLINE_POS;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class TestTableModel
        extends QualifyingBestTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn(),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new LPTableColumn("SplinePos")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r1(applet, context))/*,
            new LPTableColumn("RDC")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r2(applet, context)),
            new LPTableColumn("Stationary")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r3(applet, context)),
            new LPTableColumn("Count")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r4(applet, context)),
            new LPTableColumn("OI")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r5(applet, context)),
            new LPTableColumn("S3C")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r6(applet, context))
         */
        };
    }

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((c1, c2)
                        -> c1.get(REALTIME_POSITION).compareTo(c2.get(REALTIME_POSITION))
                )
                .collect(toList());
    }

    private void r1(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String text = String.format("%.5f", stats.get(SPLINE_POS));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r2(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String text = String.format("%.5f", stats.get(RACE_DISTANCE_COMPLEX));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);

    }

    private void r3(PApplet applet, LPTable.RenderContext context) {
        int timeInPits = ((CarStatistics) context.object).get(PITLANE_TIME_STATIONARY);
        if (timeInPits != 0) {
            String text = TimeUtils.asDurationShort(timeInPits);
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

    private void r4(PApplet applet, LPTable.RenderContext context) {
        int count = ((CarStatistics) context.object).get(PITLANE_COUNT);
        boolean isAccurate = ((CarStatistics) context.object).get(PITLANE_COUNT_ACCURATE);

        String text = String.format("%d", count)
                + (isAccurate ? "" : "*");
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r5(PApplet applet, LPTable.RenderContext context) {
        int over = ((CarStatistics) context.object).get(OVERTAKE_INDICATOR);
        String text = String.valueOf(over);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r6(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_THREE_CALC);
        String text = "";
        if (time != 0) {
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

}
