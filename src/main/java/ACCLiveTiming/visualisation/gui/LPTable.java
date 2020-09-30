/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 * @param <T> The class this table is representing.
 */
public class LPTable<T extends LPTable.Entry> extends LPComponent {

    /**
     * List of the avaiable columns in this table.
     */
    private List<Column> columns = new LinkedList<>();
    /**
     * List of current entries in the table.
     */
    private List<T> entries = new LinkedList<>();
    /**
     * The ammount of visible entries at the moment.
     */
    private int visibleEntries = 0;

    @Override
    public void draw() {
        applet.fill(50);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());

        //Draw headers
        for (Column c : columns) {
            applet.fill(30);
            applet.stroke(255);
            applet.rect(c.xOffset, 0, c.size, LookAndFeel.get().LINE_HEIGHT);
            applet.fill(255);
            applet.textAlign(c.alignment, CENTER);
            int padding = (int) (LookAndFeel.get().LINE_HEIGHT * 0.2f);
            if (c.alignment == LEFT) {
                applet.text(c.head, c.xOffset + padding, LookAndFeel.get().LINE_HEIGHT / 2f);
            }
            if (c.alignment == CENTER) {
                applet.text(c.head, c.xOffset + c.size / 2f, LookAndFeel.get().LINE_HEIGHT / 2f);
            }
            if (c.alignment == RIGHT) {
                applet.text(c.head, c.xOffset + c.size - padding, LookAndFeel.get().LINE_HEIGHT / 2f);
            }
        }

        //Draw entries
        int rowLimit = Math.min(visibleEntries, entries.size());
        for(int i=0; i<rowLimit; i++){
            for(Column c : columns){
                applet.fill(applet.color(100,0,0));
                applet.rect(c.xOffset,(i+1)*LookAndFeel.get().LINE_HEIGHT,
                        c.size, LookAndFeel.get().LINE_HEIGHT);
                applet.fill(255);
                applet.textAlign(LEFT,CENTER);
                applet.text(c.contentFunction.apply(entries.get(i)),c.xOffset, (i+1.5f)*LookAndFeel.get().LINE_HEIGHT);
            }
        }
    }

    public void addColumn(String head,
            int size,
            boolean dynamicSize,
            int alignment,
            Function<T, String> content) {
        columns.add(new Column(head, size, dynamicSize, alignment, content));
    }

    public void addEntry(T entry) {
        entries.add(entry);
    }

    public void removeEntry(T entry) {
        entries.remove(entry);
    }

    public void setEntries(List<T> entries) {
        this.entries = entries;
    }

    @Override
    public void onResize(int w, int h) {
        calculateColumnWidths();
        
        visibleEntries = (int)Math.floor(getHeight() / LookAndFeel.get().LINE_HEIGHT) - 1;
    }

    private void calculateColumnWidths() {
        int minSize = 0;
        for (Column c : columns) {
            minSize += c.minSize;
        }
        if (getWidth() < minSize) {
            int xoffset = 0;
            for (Column c : columns) {
                c.size = getWidth() / columns.size();
                c.xOffset = xoffset;
                xoffset += c.size;
            }
        } else {
            float staticSize = 0;
            int dynamicCount = 0;
            List<Column> dynamicColumns = new LinkedList<>();
            List<Column> staticColumns = new LinkedList<>();
            for (Column c : columns) {
                if (!c.dynamicSize) {
                    staticSize += c.minSize;
                    staticColumns.add(c);
                } else {
                    dynamicColumns.add(c);
                    dynamicCount++;
                }
            }
            //resize dynamic columns
            final float dynamicSize = Math.max(getWidth() - staticSize, 0);
            for (Column c : dynamicColumns) {
                c.size = Math.max(dynamicSize / dynamicCount, c.minSize);
            }
            //resize static columns in case they were shrunken.
            for (Column c : staticColumns) {
                c.size = c.minSize;
            }
            //calculate offset for each column.
            float xOffset = 0;
            for (Column c : columns) {
                c.xOffset = xOffset;
                xOffset += c.size;
            }
        }
    }

    public class Column {

        public final String head;
        public float size;
        public float xOffset;
        public float minSize;
        public final boolean dynamicSize;
        public final int alignment;
        public final Function<T, String> contentFunction;

        public Column(String head, float size, boolean dynamicSize, int alignment,
                Function<T, String> contentFunction) {
            this.head = head;
            this.size = size;
            this.minSize = size;
            this.dynamicSize = dynamicSize;
            this.alignment = alignment;
            this.contentFunction = contentFunction;
        }
    }

    public static class Entry {
    }
    
}
