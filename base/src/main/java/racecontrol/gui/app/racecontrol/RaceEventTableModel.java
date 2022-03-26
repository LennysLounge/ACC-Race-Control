/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol;

import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.client.extension.raceevent.entries.RaceEventEntry;
import racecontrol.client.data.SessionId;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTable.RenderContext;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.gui.lpui.table.LPTableModel;
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

    private SessionId activeSessionId;

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
            .setCellRenderer(replayButtonRenderer),
            new LPTableColumn("Time")
            .setMinWidth(TEXT_SIZE * 5)
            .setMaxWidth(TEXT_SIZE * 5)
            .setCellRenderer(replayTimeRenderer),
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
        if (row >= getRowCount()) {
            return;
        }
        if (column == 2) {
            //info column clicked.
            infoColumnClicked.onClick(entries.get(row), mouseX, mouseY);
        } else if (column == 3) {
            replayButtonClicked.onClick(entries.get(row), mouseX, mouseY);
        }
    }

    public void setSessionId(SessionId sessionId) {
        this.activeSessionId = sessionId;

    }

    public void addEntry(RaceEventEntry entry) {
        entries.add(entry);
        entryAdded(entries.indexOf(entry));
    }

    public RaceEventEntry getEntry(int index) {
        if (index < entries.size()) {
            return entries.get(index);
        }
        return null;
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
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(time, context.width / 2f, context.height / 2f);
    };

    private final LPTable.CellRenderer typeRenderer = (
            PApplet applet,
            RenderContext context) -> {
        RaceEventEntry entry = (RaceEventEntry) context.object;
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
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
        applet.textFont(LookAndFeel.fontRegular());
        if (entry.isHasReplay() && entry.getSessionId() == activeSessionId) {
            if (entry.getReplayTime() == -1) {
                applet.fill(COLOR_WHITE);
                applet.textAlign(LEFT, CENTER);
                applet.text("Replay time not known", context.height / 2f, context.height / 2f);
                return;
            }

            if (context.isMouseOverColumn && context.isMouseOverRow) {
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
        if (entry.isHasReplay() && entry.getSessionId() == activeSessionId) {
            if (entry.getReplayTime() == -1) {
                return;
            }
            String time = TimeUtils.asDuration(entry.getReplayTime());
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(time, context.width / 2f, context.height / 2f);
        }
    };

}
