/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.extensions.ExtensionPanel;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 */
public class IncidentPanel extends ExtensionPanel {

    private final IncidentExtension extension;

    private int scroll = 0;

    private int visibleRows = 0;

    private List<Accident> incidents = new LinkedList<>();

    private final Column[] columns = {
        new Column("", 0.4f, false, LEFT),
        new Column("Nr.", 1, false, CENTER),
        new Column("Session Time", 6.4f, false, LEFT),
        new Column("#", 1.25f, true, LEFT)
    };

    public IncidentPanel(IncidentExtension extension) {
        this.extension = extension;
        this.displayName = "INCIDENTS";
    }

    @Override
    public void drawPanel() {
        LookAndFeel laf = LookAndFeel.get();
        incidents = extension.getAccidents();
        //Draw Button row.
        applet.fill(laf.COLOR_DARK_DARK_GRAY);
        applet.rect(0, 0, width, laf.LINE_HEIGHT);
        applet.fill(laf.COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.text("Click to add empty incident:", columns[1].xOffset*laf.LINE_HEIGHT, laf.LINE_HEIGHT*0.5f);

        //Draw Header
        applet.fill(laf.COLOR_DARK_DARK_GRAY);
        applet.rect(0, laf.LINE_HEIGHT, width, laf.LINE_HEIGHT);
        applet.fill(laf.COLOR_WHITE);
        for (Column column : columns) {
            drawCellText(column.head, column, 1, laf.COLOR_WHITE);
        }

        //Draw background
        int rows = Math.min(visibleRows, incidents.size() + 1);
        int yOffset = laf.LINE_HEIGHT*2;
        for (int i = 2; i < rows; i++) {
            applet.fill((i + scroll) % 2 == 0 ? laf.COLOR_DARK_DARK_GRAY : laf.COLOR_DARK_GRAY);
            applet.rect(0, yOffset, width, laf.LINE_HEIGHT);
            yOffset += laf.LINE_HEIGHT;
        }
        applet.fill(laf.COLOR_DARK_GRAY);
        applet.rect(0, yOffset, width, height - yOffset);

        int n = 2;
        int scrollSkip = scroll;
        for (Accident accident : incidents) {
            if (scrollSkip-- > 0) {
                continue;
            }

            switch (accident.getSessionID().getType()) {
                case PRACTICE:
                    drawCellBackground(columns[1], n, laf.COLOR_PRACTICE);
                    break;
                case QUALIFYING:
                    drawCellBackground(columns[1], n, laf.COLOR_QUALIFYING);
                    break;
                case RACE:
                    drawCellBackground(columns[1], n, laf.COLOR_RACE);
                    break;
                default:
                    break;
            }
            drawCellText(String.valueOf(accident.getIncidentNumber()), columns[1], n, laf.COLOR_WHITE);
            drawCellText(TimeUtils.asDuration(accident.getEarliestTime()), columns[2], n, laf.COLOR_WHITE);

            //Draw car numbres
            float x = columns[3].xOffset * laf.LINE_HEIGHT;
            for (int carId : accident.getCars()) {
                CarInfo car = extension.getModel().getCar(carId);
                String carNumber = String.valueOf(extension.getModel().getCar(carId).getCarNumber());

                int background_color = 0;
                int text_color = 0;

                switch (car.getDriver().getCategory()) {
                    case BRONZE:
                        background_color = laf.COLOR_RED;
                        text_color = laf.COLOR_BLACK;
                        break;
                    case SILVER:
                        background_color = laf.COLOR_GRAY;
                        text_color = laf.COLOR_WHITE;
                        break;
                    case GOLD:
                    case PLATINUM:
                        background_color = laf.COLOR_WHITE;
                        text_color = laf.COLOR_BLACK;
                        break;
                }

                float w = laf.LINE_HEIGHT * 1.25f;
                applet.fill(background_color);
                applet.rect(x + 1, laf.LINE_HEIGHT * n + 1, w - 2, laf.LINE_HEIGHT - 2);
                applet.fill(text_color);
                applet.textAlign(CENTER, CENTER);
                applet.text(carNumber, x + w / 2, laf.LINE_HEIGHT * (n + 0.5f));
                x += w;
            }
            n++;
        }

        //Draw scroll bar.
        float barWidth = 0.4f * laf.LINE_HEIGHT;
        float maxBarHeight = height - laf.LINE_HEIGHT;
        float itemHeight = maxBarHeight / Math.max(incidents.size(), 1);
        float barHeight = Math.min(itemHeight * (visibleRows - 1), maxBarHeight);
        applet.fill(laf.COLOR_DARK_DARK_GRAY);
        applet.rect(0, laf.LINE_HEIGHT, barWidth, maxBarHeight);
        applet.fill(laf.COLOR_RED);
        applet.rect(barWidth * 0.2f, laf.LINE_HEIGHT + scroll * itemHeight, barWidth * 0.6f, barHeight);
    }

    @Override
    public void mouseWheel(int count) {
        int min = 0;
        int max = Math.max(incidents.size() - (visibleRows - 1), 0);
        scroll = Math.min(Math.max(scroll + count, min), max);
        applet.forceRedraw();
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
        calculateColumnWidths();
        visibleRows = h / LookAndFeel.get().LINE_HEIGHT;
    }

    private void drawCellBackground(Column c, int y, int color) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;
        applet.fill(color);
        applet.rect(c.xOffset * lineHeight + 1, y * lineHeight + 1,
                c.size * lineHeight - 2, lineHeight - 2);
    }

    private void drawCellText(String text, Column c, int y, int color) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;

        float xoffset = c.xOffset;
        switch (c.alignment) {
            case LEFT:
                xoffset += 0.2f;
                break;
            case RIGHT:
                xoffset += c.size - 0.2f;
                break;
            case CENTER:
                xoffset += c.size / 2;
                break;
        }
        applet.fill(color);
        applet.textAlign(c.alignment, CENTER);
        applet.text(text, xoffset * lineHeight, y * lineHeight + lineHeight / 2);
    }

    private void calculateColumnWidths() {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;
        float w = width / lineHeight;

        float staticSize = 0;
        int dynamicCount = 0;
        List<Column> dynamicColumns = new LinkedList<>();
        for (Column c : columns) {
            if (!c.dynamicSize) {
                staticSize += c.size;
            } else {
                dynamicColumns.add(c);
                dynamicCount++;
            }
        }
        final float dynamicSize = Math.max(w - staticSize, 0);
        final int count = dynamicCount;
        dynamicColumns.forEach(column -> column.size = Math.max(dynamicSize / count, column.minSize));

        //calculate offset for each column.
        float xOffset = 0;
        for (Column c : columns) {
            c.xOffset = xOffset;
            xOffset += c.size;
        }
    }

    private class Column {

        public final String head;
        public float size;
        public float xOffset;
        public float minSize;
        public final boolean dynamicSize;
        public final int alignment;

        public Column(String head, float size, boolean dynamicSize, int alignment) {
            this.head = head;
            this.size = size;
            this.minSize = size;
            this.dynamicSize = dynamicSize;
            this.alignment = alignment;
        }

    }

}
