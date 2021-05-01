/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.velocitymap;

import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.networking.data.RealtimeInfo;
import static base.screen.networking.enums.CarLocation.TRACK;
import static base.screen.networking.enums.LapType.REGULAR;
import base.screen.networking.events.RealtimeCarUpdate;
import static base.screen.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import base.screen.visualisation.gui.LPButton;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPLabel;
import java.util.ArrayList;
import java.util.List;
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
    public void draw() {
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
    public void onResize(int w, int h) {
        PGraphics g = applet.createGraphics(w - 40, h - LINE_HEIGHT - 20);
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
