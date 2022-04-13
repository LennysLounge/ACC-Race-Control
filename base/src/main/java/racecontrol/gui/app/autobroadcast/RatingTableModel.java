/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.autobroadcast;

import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.client.extension.autobroadcast.Entry;
import racecontrol.client.model.Car;
import static racecontrol.gui.LookAndFeel.COLOR_BLUE;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_BLUE;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.LiveTimingTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;
import racecontrol.gui.lpui.table.LPTable.RenderContext;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class RatingTableModel
        extends LiveTimingTableModel {

    private List<Entry> entriesNew = new ArrayList<>();

    public RatingTableModel() {
    }

    public void setEntriesNew(List<Entry> entriesNew) {
        this.entriesNew = entriesNew;
    }

    @Override
    public int getRowCount() {
        return entriesNew.size();
    }

    @Override
    public Object getValueAt(int column, int row) {
        Entry entry = entriesNew.get(row);
        if (column <= 3) {
            return entry.car;
        }
        return entry;
    }

    @Override
    public void sort() {
    }

    @Override
    public String getName() {
        return "";
    }

    public void sortPosition() {
        entriesNew = entriesNew.stream()
                .sorted((c1, c2) -> Integer.compare(c1.car.realtimePosition, c2.car.realtimePosition))
                .collect(toList());
    }

    public void sortRating() {
        entriesNew = entriesNew.stream()
                .sorted((c1, c2) -> Float.compare(c2.getRating(), c1.getRating()))
                .collect(toList());
    }

    public Entry getEntryNew(int index) {
        return entriesNew.get(index);
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn(),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new LPTableColumn("Proximity")
            .setMinWidth(100)
            .setCellRenderer(this::proximityRenderer),
            new LPTableColumn("Position")
            .setMinWidth(100)
            .setCellRenderer(this::positionRenderer),
            new LPTableColumn("Focus")
            .setMinWidth(100)
            .setCellRenderer(this::focusRenderer),/*
            new LPTableColumn("Pace")
            .setMinWidth(100)
            .setCellRenderer(this::paceRenderer),
            new LPTableColumn("Pace Focus")
            .setMinWidth(100)
            .setCellRenderer(this::paceFocusRenderer),
            new LPTableColumn("Slow")
            .setMaxWidth(150)
            .setMinWidth(150)
            .setCellRenderer(this::focusSlowRenderer),*/
            new LPTableColumn("Rating")
            .setMinWidth(100)
            .setCellRenderer(this::ratingRenderer),};
    }

    private void proximityRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValueShrink(applet, context, entry.proximity);
    }

    private void positionRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.position);
    }

    private void focusRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.focus);
    }

    private void ratingRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValueDouble(applet, context, entry.getRating(), entry.getRatingNoFocus());
    }

    private void renderValue(PApplet applet, RenderContext context, float value) {
        float w = context.width * Math.max(0, Math.min(1, Math.abs(value)));
        if (value < 0) {
            applet.fill(COLOR_RED);
            applet.rect(context.width - w, 0, w, context.height);
        } else {
            applet.fill(COLOR_BLUE);
            applet.rect(0, 0, w, context.height);
        }

        applet.textAlign(CENTER, CENTER);
        applet.fill(COLOR_WHITE);
        applet.text(String.format("%.2f", value), context.width / 2, context.height / 2);
    }

    private void renderValueShrink(PApplet applet, RenderContext context, float value) {
        float w = context.width * Math.max(0, Math.min(1, shrink(Math.abs(value))));
        if (value < 0) {
            applet.fill(COLOR_RED);
            applet.rect(context.width - w, 0, w, context.height);
        } else {
            applet.fill(COLOR_BLUE);
            applet.rect(0, 0, w, context.height);
        }

        applet.textAlign(CENTER, CENTER);
        applet.fill(COLOR_WHITE);
        applet.text(String.format("%.2f", value), context.width / 2, context.height / 2);
    }

    private void renderValueDouble(PApplet applet,
            RenderContext context,
            float primary,
            float secondary) {
        float w = context.width * Math.max(0, Math.min(1, shrink(Math.abs(secondary))));
        if (secondary < 0) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(context.width - w, 0, w, context.height);
        } else {
            applet.fill(COLOR_DARK_BLUE);
            applet.rect(0, 0, w, context.height);
        }
        w = context.width * Math.max(0, Math.min(1, shrink(Math.abs(primary))));
        if (primary < 0) {
            applet.fill(COLOR_RED);
            applet.rect(context.width - w, 0, w, context.height);
        } else {
            applet.fill(COLOR_BLUE);
            applet.rect(0, 0, w, context.height);
        }

        applet.textAlign(CENTER, CENTER);
        applet.fill(COLOR_WHITE);
        applet.text(String.format("%.2f", primary), context.width / 2, context.height / 2);
    }

    private float shrink(float v) {
        return 1f - 1f / (v + 1);
    }

}
