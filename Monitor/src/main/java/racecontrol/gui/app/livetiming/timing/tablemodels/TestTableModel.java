/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_ONE_CALC;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_THREE_CALC;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_SECTOR_TWO_CALC;
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
            new LPTableColumn("S1")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r1(applet, context)),
            new LPTableColumn("S1C")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r2(applet, context)),
            new LPTableColumn("S2")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r3(applet, context)),
            new LPTableColumn("S2C")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r4(applet, context)),
            new LPTableColumn("S3")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r5(applet, context)),
            new LPTableColumn("S3C")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r6(applet, context))
        };
    }

    @Override
    public String getName() {
        return "Test";
    }

    private void r1(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_ONE);
        String text = "";
        if(time != 0){
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r2(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_ONE_CALC);
        String text = "";
        if(time != 0){
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r3(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_TWO);
        String text = "";
        if(time != 0){
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r4(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_TWO_CALC);
        String text = "";
        if(time != 0){
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r5(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_THREE);
        String text = "";
        if(time != 0){
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r6(PApplet applet, LPTable.RenderContext context) {
        int time = ((CarStatistics) context.object).get(CURRENT_SECTOR_THREE_CALC);
        String text = "";
        if(time != 0){
            text = TimeUtils.asSeconds(time);
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

}
