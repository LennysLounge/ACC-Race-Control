/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

/**
 *
 * @author Leonard
 */
public class Column {

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

    public Column(String header) {
        this.header = header;
    }

    public Column setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
        return this;
    }

    public Column setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Column setMinWidth(float minWidth) {
        this.minWidth = minWidth;
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
}
