/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static racecontrol.LookAndFeel.COLOR_GRAY;
import static racecontrol.LookAndFeel.COLOR_RED;
import static racecontrol.LookAndFeel.COLOR_WHITE;
import static racecontrol.LookAndFeel.TEXT_SIZE;
import racecontrol.app.racecontrol.entries.RaceEventEntry;
import racecontrol.lpgui.gui.LPTable;
import racecontrol.lpgui.gui.LPTable.RenderContext;
import racecontrol.lpgui.gui.LPTableColumn;
import racecontrol.lpgui.gui.LPTableModel;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class RaceEventTableModel
        extends LPTableModel {

    private final List<RaceEventEntry> entries = new LinkedList<>();

    @FunctionalInterface
    public interface ClickAction {
        void onClick(RaceEventEntry entry, int mouseX, int mouseY);
    }

    private ClickAction replayButtonClicked = (RaceEventEntry entry, int mouseX, int mouseY) -> {
    };
    private ClickAction infoColumnClicked = (RaceEventEntry entry, int mouseX, int mouseY) -> {
    };

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("Time")
            .setMinWidth(TEXT_SIZE * 5)
            .setMaxWidth(TEXT_SIZE * 5)
            .setCellRenderer(sessionTimeRenderer),
            new LPTableColumn("Info")
            .setMaxWidth(300)
            .setCellRenderer(typeRenderer),
            new LPTableColumn("")
            .setMaxWidth(400)
            .setCellRenderer(infoRenderer),
            new LPTableColumn("Replay")
            .setMinWidth(TEXT_SIZE * 5)
            .setMaxWidth(TEXT_SIZE * 5)
            .setCellRenderer(LPTableColumn.nullRenderer),
            new LPTableColumn("Time")
            .setMinWidth(TEXT_SIZE * 5)
            .setMaxWidth(TEXT_SIZE * 5)
            .setCellRenderer(LPTableColumn.nullRenderer),
            new LPTableColumn("")
            .setCellRenderer(LPTableColumn.nullRenderer)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        return entries.get(row);
    }

    @Override
    public void onClick(int column, int row, int mouseX, int mouseY) {
        if(column == 2){
            //info column clicked.
            infoColumnClicked.onClick(entries.get(row), mouseX, mouseY);
        }
        else if(column == 3){
            replayButtonClicked.onClick(entries.get(row), mouseX, mouseY);
        }
    }

    public void addEntry(RaceEventEntry entry) {
        entries.add(entry);
    }

    public void setReplayClickAction(ClickAction action) {
        this.replayButtonClicked = action;
    }
    
    public void setInfoColumnAction(ClickAction action) {
        this.infoColumnClicked = action;
    }

    private final LPTable.CellRenderer sessionTimeRenderer = (
            PApplet applet,
            RenderContext context) -> {
        RaceEventEntry entry = (RaceEventEntry) context.object;
        String time = TimeUtils.asDuration(entry.getSessionTime());
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(time, context.width / 2f, context.height / 2f);
    };

    private final LPTable.CellRenderer typeRenderer = (
            PApplet applet,
            RenderContext context) -> {
        RaceEventEntry entry = (RaceEventEntry) context.object;
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.text(entry.getTypeDescriptor(), context.height / 2f, context.height / 2f);
    };

    private final LPTable.CellRenderer infoRenderer = (
            PApplet applet,
            RenderContext context) -> {
        RaceEventEntry entry = (RaceEventEntry) context.object;
        entry.getInfoRenderer().render(applet, context);
    };

    private final LPTable.CellRenderer replayButtonRenderer = (
            PApplet applet,
            RenderContext context) -> {
        RaceEventEntry entry = (RaceEventEntry) context.object;
        if (entry.isHasReplay()) {
            if (context.isMouseOverColumn) {
                applet.fill(COLOR_RED);
            } else {
                applet.fill(COLOR_GRAY);
            }
            applet.rect(2, 2, context.width - 4, context.height - 4);
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.text("Replay", context.width / 2f, context.height / 2f);
        }
    };

    private final LPTable.CellRenderer replayTimeRenderer = (
            PApplet applet,
            RenderContext context) -> {
        RaceEventEntry entry = (RaceEventEntry) context.object;
        if (entry.isHasReplay()) {
            String time = TimeUtils.asDuration(entry.getSessionTime());
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.text(time, context.width / 2f, context.height / 2f);
        }
    };

}
