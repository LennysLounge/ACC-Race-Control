/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import processing.core.PGraphics;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.LPTextField;

/**
 *
 * @author Leonard
 */
public class TrackDataPanel
        extends LPContainer {

    protected final LPLabel trackNameLabel = new LPLabel("trackname");

    private final LPLabel sectorOneLabel = new LPLabel("S1");
    protected final LPTextField sectorOneTextField = new LPTextField();
    private final LPLabel sectorTwoLabel = new LPLabel("S2");
    protected final LPTextField sectorTwoTextField = new LPTextField();
    private final LPLabel sectorThreeLabel = new LPLabel("S3");
    protected final LPTextField sectorThreeTextField = new LPTextField();

    protected final LPButton saveButton = new LPButton("Save");

    protected List<Float> vMap = new ArrayList<>();
    protected List<Float> savedVMap = new ArrayList<>();
    private PGraphics vMapGraph;

    protected List<Float> dirMap = new ArrayList<>();

    private float graphHeight;
    private float graphWidth;

    public TrackDataPanel() {
        addComponent(trackNameLabel);
        sectorOneLabel.setSize(30, LINE_HEIGHT);
        addComponent(sectorOneLabel);
        sectorOneTextField.setSize(100, LINE_HEIGHT);
        addComponent(sectorOneTextField);
        sectorTwoLabel.setSize(30, LINE_HEIGHT);
        addComponent(sectorTwoLabel);
        sectorTwoTextField.setSize(100, LINE_HEIGHT);
        addComponent(sectorTwoTextField);
        sectorThreeLabel.setSize(30, LINE_HEIGHT);
        addComponent(sectorThreeLabel);
        sectorThreeTextField.setSize(100, LINE_HEIGHT);
        addComponent(sectorThreeTextField);

        saveButton.setSize(100, LINE_HEIGHT);
        addComponent(saveButton);
    }

    public void addVMapPoint(float splinePos, float speed) {
        if (vMapGraph != null) {
            float x = graphWidth * splinePos;
            float y = PApplet.map(speed, 0, 300, graphHeight, 0);
            vMapGraph.beginDraw();
            vMapGraph.point(x, y);
            vMapGraph.endDraw();
            invalidate();
        }
    }

    @Override
    public void onResize(float w, float h) {
        trackNameLabel.setPosition(10, 0);

        sectorOneLabel.setPosition(320, 0);
        sectorOneTextField.setPosition(350, 0);

        sectorTwoLabel.setPosition(470, 0);
        sectorTwoTextField.setPosition(500, 0);

        sectorThreeLabel.setPosition(620, 0);
        sectorThreeTextField.setPosition(650, 0);

        saveButton.setPosition(w - 110, 0);

        graphHeight = (getHeight() - LINE_HEIGHT - 20) / 2f;
        graphWidth = getWidth() - 20;
        PGraphics g = getApplet().createGraphics((int) graphWidth, (int) graphHeight);
        g.fill(0);
        g.beginDraw();
        g.background(0);
        if (vMapGraph != null) {
            g.image(vMapGraph, 0, 0, g.width, g.height);
        }
        g.endDraw();
        vMapGraph = g;
        vMapGraph.stroke(255, 255, 255, 50);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(0);

        // Draw velocity map.
        applet.translate(10, LINE_HEIGHT);
        applet.rect(0, 0, graphWidth, graphHeight);

        applet.image(vMapGraph, 0, 0);

        applet.stroke(100, 255, 100);
        applet.strokeWeight(2);
        applet.noFill();
        applet.beginShape();
        for (int i = 0; i < vMap.size(); i++) {
            float x = graphWidth / vMap.size() * i;
            float y = PApplet.map(vMap.get(i), 0, 300, graphHeight, 0);
            applet.vertex(x, y);
        }
        applet.endShape();

        applet.beginShape();
        for (int i = 0; i < savedVMap.size(); i++) {
            float x = graphWidth / savedVMap.size() * i;
            float y = PApplet.map(savedVMap.get(i), 0, 300, graphHeight, 0);
            applet.vertex(x, y);
        }
        applet.endShape();
        applet.strokeWeight(1);
        applet.noStroke();

        // Draw direction map
        applet.translate(0, graphHeight + 10);
        applet.fill(0);
        applet.rect(0, 0, graphWidth, graphHeight);

        applet.translate(-10, -LINE_HEIGHT - graphHeight - 10);
    }

}
