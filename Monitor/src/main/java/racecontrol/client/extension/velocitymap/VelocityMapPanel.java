/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.velocitymap;

import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
public class VelocityMapPanel
        extends LPContainer {

    private final LPLabel trackNameLabel = new LPLabel("");
    private List<Float> velocityMap = new ArrayList<>();
    private final LPButton saveButton = new LPButton("Save");
    private PGraphics graph;
    private final VelocityMapExtension extension;

    public VelocityMapPanel(VelocityMapExtension extension) {
        this.extension = extension;
        setName("VelocityMap");

        trackNameLabel.setPosition(240, 0);
        addComponent(trackNameLabel);

        saveButton.setAction(() -> {
            extension.saveVelocityMap();
        });
        saveButton.setPosition(20, 0);
        saveButton.setSize(200, LINE_HEIGHT);
        addComponent(saveButton);

    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.image(graph, 20, LINE_HEIGHT);

        applet.noFill();
        applet.stroke(255);
        applet.beginShape();
        for (int i = 0; i < velocityMap.size(); i++) {
            float x = (getWidth() - 40) / velocityMap.size() * i;
            float y = (getHeight() - LINE_HEIGHT - 20) / 300 * velocityMap.get(i);
            applet.vertex(20 + x, getHeight() - 20 - y);
        }
        applet.endShape();
    }

    @Override
    public void onResize(float w, float h) {
        PGraphics g = getApplet().createGraphics((int)w - 40, (int)h - LINE_HEIGHT - 20);
        g.fill(0);
        g.beginDraw();
        g.background(0);
        if (graph != null) {
            g.image(graph, 0, 0, g.width, g.height);
        }
        g.endDraw();
        graph = g;
        //graph.rect(0, 0, graph.width, graph.height);
        graph.stroke(255, 255, 255, 50);
    }

    public void setTrackName(String name) {
        trackNameLabel.setText(name);
    }

    public void setVelocityMap(List<Float> map) {
        velocityMap = map;
    }

    public void onRealtimeCarUpdate(float splinePos, float speed) {
        float w = getWidth() - 40;
        float h = getHeight() - 20 - LINE_HEIGHT;
        float x = w * splinePos;
        float y = h - h / 300 * speed;
        y += x % (h / 300);
        graph.beginDraw();
        graph.point(x, y);
        graph.endDraw();
        invalidate();
    }
}
