/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_CAR_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_SPLIT;
import static racecontrol.client.extension.statistics.CarProperties.RACE_DISTANCE_BEHIND;
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
        extends QualifyingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Gap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> gapRenderer(applet, context)),
            new LPTableColumn("To leader")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> gapLeaderRenderer(applet, context)),
            new LPTableColumn("to car")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> gapCarRenderer(applet, context)),
            new LPTableColumn("l behind")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> t1(applet, context)),
            new LPTableColumn("split")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> t2(applet, context)),
            new LPTableColumn("rd")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> t3(applet, context))
        };
    }

    @Override
    public String getName() {
        return "Test";
    }

    private void gapRenderer(PApplet applet, LPTable.RenderContext context) {
        String text = TimeUtils.asGap(((CarStatistics) context.object).get(GAP_TO_POSITION_AHEAD));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void gapLeaderRenderer(PApplet applet, LPTable.RenderContext context) {
        String text = TimeUtils.asGap(((CarStatistics) context.object).get(GAP_TO_LEADER));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void gapCarRenderer(PApplet applet, LPTable.RenderContext context) {
        String text = TimeUtils.asGap(((CarStatistics) context.object).get(GAP_TO_CAR_AHEAD));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void t1(PApplet applet, LPTable.RenderContext context) {
        String text = String.valueOf(((CarStatistics) context.object).get(LAPS_BEHIND_LEADER));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void t2(PApplet applet, LPTable.RenderContext context) {
        String text = String.valueOf(((CarStatistics) context.object).get(LAPS_BEHIND_SPLIT));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void t3(PApplet applet, LPTable.RenderContext context) {
        String text = String.valueOf(((CarStatistics) context.object).get(RACE_DISTANCE_BEHIND));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

}
