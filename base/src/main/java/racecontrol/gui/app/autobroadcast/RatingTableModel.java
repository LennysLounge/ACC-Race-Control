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
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.StatisticsExtension;
import static racecontrol.gui.LookAndFeel.COLOR_BLUE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.LiveTimingTableModel;
import racecontrol.gui.lpui.table.LPTable.RenderContext;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class RatingTableModel
        extends LiveTimingTableModel {

    private final StatisticsExtension statistics;

    private List<Entry> entriesNew = new ArrayList<>();

    public RatingTableModel() {
        statistics = StatisticsExtension.getInstance();
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
            return statistics.getCar(entry.getCarInfo().getCarId());
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
                .sorted((c1, c2) -> {
                    var c1Stat = statistics.getCar(c1.getCarInfo().getCarId());
                    var c2Stat = statistics.getCar(c2.getCarInfo().getCarId());
                    return c1Stat.get(REALTIME_POSITION).compareTo(c2Stat.get(REALTIME_POSITION));
                })
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
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Proximit")
            .setMaxWidth(150)
            .setMinWidth(150)
            .setCellRenderer(this::proximityRenderer),
            new LPTableColumn("Proximit D")
            .setMaxWidth(150)
            .setMinWidth(150)
            .setCellRenderer(this::proximityDeltaRenderer),
            new LPTableColumn("Overtake")
            .setMaxWidth(150)
            .setMinWidth(150)
            .setCellRenderer(this::overtakeRenderer),
            new LPTableColumn("Change")
            .setMaxWidth(150)
            .setMinWidth(150)
            .setCellRenderer(this::changeRenderer),
            new LPTableColumn("Rating")
            .setMaxWidth(150)
            .setMinWidth(150)
            .setCellRenderer(this::ratingRenderer),};
    }

    private void proximityRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getProximity());
    }

    private void proximityDeltaRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getProximityDelta());
    }

    private void overtakeRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getOvertake());
    }

    private void changeRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getChange());
    }

    private void ratingRenderer(PApplet applet, RenderContext context) {
        Entry entry = (Entry) context.object;
        renderValue(applet, context, entry.getRating());
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
