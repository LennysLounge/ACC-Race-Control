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
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_COMPLEX;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_SIMPLE;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.USE_REALTIME_POS;
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
            new LPTableColumn("RDS")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r1(applet, context)),
            new LPTableColumn("RDC")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r2(applet, context)),
            new LPTableColumn("RTPos")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r3(applet, context)),
            new LPTableColumn("Diff")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r4(applet, context)),
            new LPTableColumn("Use RTPos")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r5(applet, context))/*,
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
        float rds = ((CarStatistics) context.object).get(RACE_DISTANCE_SIMPLE);
        String text = String.format("%.5f", rds);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r2(PApplet applet, LPTable.RenderContext context) {
        float rds = ((CarStatistics) context.object).get(RACE_DISTANCE_COMPLEX);
        String text = String.format("%.5f", rds);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r3(PApplet applet, LPTable.RenderContext context) {
        int pos = ((CarStatistics) context.object).get(REALTIME_POSITION);

        String text = String.format("%d", pos);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r4(PApplet applet, LPTable.RenderContext context) {
        int rtPos = ((CarStatistics) context.object).get(REALTIME_POSITION);
        int pos = ((CarStatistics) context.object).get(POSITION);
        int diff = pos - rtPos;

        String text = String.format("%d", diff);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r5(PApplet applet, LPTable.RenderContext context) {
        boolean over = ((CarStatistics) context.object).get(USE_REALTIME_POS);
        String text = over ? "true" : "false";
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
