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
import static racecontrol.gui.LookAndFeel.COLOR_BLUE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.LiveTimingTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
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
            return entry.getCar();
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
                .sorted((c1, c2) -> Integer.compare(c1.getCar().realtimePosition, c2.getCar().realtimePosition))
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
            new PitFlagColumn(),
            new CarNumberColumn(),
            new LPTableColumn("Proximity")
            .setMinWidth(100)
            .setCellRenderer(this::proximityRenderer),
            new LPTableColumn("Pack")
            .setMinWidth(100)
            .setCellRenderer(this::packRenderer),
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

    private void proximityFrontRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getProximityFront());
    }

    private void proximityRearRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getProximityRear());
    }

    private void proximityRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getProximity());
    }

    private void positionRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getPosition());
    }

    private void focusRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getFocus());
    }

    private void focusSlowRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getFocusSlow());
    }

    private void ratingRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getRating());
    }

    private void packRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getPack());
    }

    private void packProximityRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getPackProximity());
    }

    private void packFrontRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getPackFront());
    }

    private void paceRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getPace());
    }

    private void paceFocusRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getPaceFocus());
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

}
