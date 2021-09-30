/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.client.data.enums.CarLocation;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_THREE_CALC;
import static racecontrol.client.extension.statistics.CarProperties.OVERTAKE_INDICATOR;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT_ACCURATE;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME_STATIONARY;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPTable;
import racecontrol.gui.lpui.LPTableColumn;
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
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Location")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r1(applet, context)),
            new LPTableColumn("Pit")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r2(applet, context)),
            new LPTableColumn("Stationary")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r3(applet, context)),
            new LPTableColumn("Count")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r4(applet, context))/*,
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
        CarLocation location = ((CarStatistics) context.object).get(CAR_LOCATION);
        String text = location.name();
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r2(PApplet applet, LPTable.RenderContext context) {
        int timeInPits = ((CarStatistics) context.object).get(PITLANE_TIME);
        if (timeInPits != 0) {
            String text = TimeUtils.asDurationShort(timeInPits);
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.text(text, context.width / 2f, context.height / 2f);
        }
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
