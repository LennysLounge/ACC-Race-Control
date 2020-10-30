/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LPTableColumn {

    /**
     * Header for this column.
     */
    private String header;
    /**
     * Growthrate describes how fast it grows into the available space.
     */
    private float growthRate = 1;
    /**
     * Maximum size for this column.
     */
    private float maxWidth = Float.MAX_VALUE;
    /**
     * Minimum size for this column.
     */
    private float minWidth = 0;

    private LPTable.CellRenderer renderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        applet.fill(255);
        applet.textAlign(LEFT, CENTER);
        applet.text(object.toString(), height / 2, height / 2);
    };

    public LPTableColumn(String header) {
        this.header = header;
    }

    public LPTableColumn setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
        return this;
    }

    public LPTableColumn setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public LPTableColumn setMinWidth(float minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public LPTableColumn setCellRenderer(LPTable.CellRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public float getMinWidth() {
        return minWidth;
    }

    public LPTable.CellRenderer getRenderer() {
        return renderer;
    }
}
