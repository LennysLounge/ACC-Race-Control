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
import racecontrol.client.data.RealtimeInfo;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
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

    protected final LPButton saveAllButton = new LPButton("Save All");
    protected final LPButton saveVMapButton = new LPButton("Save vMap");
    protected final LPButton saveDMapButton = new LPButton("Save dMap");
    protected final LPButton saveSectorsButton = new LPButton("Save Sec");

    protected List<Float> vMap = new ArrayList<>();
    protected List<Float> savedVMap = new ArrayList<>();
    private PGraphics vMapGraph;

    protected List<Float> dirMap = new ArrayList<>();
    protected List<Float> savedDirMap = new ArrayList<>();
    private PGraphics dirMapGraph;

    private float graphHeight;
    private float graphWidth;

    private List<RealtimeInfo> carStates = new ArrayList<>();

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

        saveAllButton.setSize(100, LINE_HEIGHT);
        addComponent(saveAllButton);

        saveVMapButton.setSize(100, LINE_HEIGHT);
        addComponent(saveVMapButton);

        saveDMapButton.setSize(100, LINE_HEIGHT);
        addComponent(saveDMapButton);

        saveSectorsButton.setSize(100, LINE_HEIGHT);
        addComponent(saveSectorsButton);
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

        saveAllButton.setPosition(w - 110, 0);
        saveVMapButton.setPosition(w - 220, 0);
        saveDMapButton.setPosition(w - 330, 0);
        saveSectorsButton.setPosition(w - 440, 0);

        graphHeight = (getHeight() - LINE_HEIGHT - 20) / 2f;
        graphWidth = getWidth() - 20;
        resizeGraphs();
    }

    private void resizeGraphs() {
        PGraphics g = getApplet().createGraphics((int) graphWidth, (int) graphHeight);
        g.fill(0);
        g.beginDraw();
        g.background(0);
        if (vMapGraph != null) {
            g.image(vMapGraph, 0, 0, g.width, g.height);
        }
        g.endDraw();
        vMapGraph = g;
        vMapGraph.strokeWeight(3);
        vMapGraph.stroke(255, 255, 255, 50);
        g = getApplet().createGraphics((int) graphWidth, (int) graphHeight);
        g.fill(0);
        g.beginDraw();
        g.background(0);
        if (dirMapGraph != null) {
            g.image(dirMapGraph, 0, 0, g.width, g.height);
        }
        g.endDraw();
        dirMapGraph = g;
        dirMapGraph.strokeWeight(3);
        dirMapGraph.stroke(255, 255, 255, 50);
    }

    public void drawCarState(RealtimeInfo info) {
        carStates.add(info);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(0);

        // Draw velocity map.
        applet.translate(10, LINE_HEIGHT);
        applet.rect(0, 0, graphWidth, graphHeight);

        // draw vmap markers
        List<RealtimeInfo> carStateSafe = new ArrayList<>(carStates);
        carStates.clear();
        for (RealtimeInfo info : carStateSafe) {
            float x = graphWidth * info.getSplinePosition();
            float y = PApplet.map(info.getKMH(), 0, 300, graphHeight, 0);
            vMapGraph.beginDraw();
            vMapGraph.point(x, y);
            vMapGraph.endDraw();
        }

        applet.image(vMapGraph, 0, 0);

        // draw current VMap
        applet.stroke(100, 255, 100);
        applet.noFill();
        applet.beginShape();
        for (int i = 0; i < vMap.size(); i++) {
            float x = graphWidth / vMap.size() * i;
            float y = PApplet.map(vMap.get(i), 0, 300, graphHeight, 0);
            applet.vertex(x, y);
        }
        applet.endShape();

        // draw saved vmap.
        applet.stroke(100, 100, 255);
        applet.beginShape();
        for (int i = 0; i < savedVMap.size(); i++) {
            float x = graphWidth / savedVMap.size() * i;
            float y = PApplet.map(savedVMap.get(i), 0, 300, graphHeight, 0);
            applet.vertex(x, y);
        }
        applet.endShape();
        applet.noStroke();

        // draw car positions
        applet.fill(COLOR_WHITE);
        for (RealtimeInfo info : carStateSafe) {
            float x = graphWidth * info.getSplinePosition();
            float y = PApplet.map(info.getKMH(), 0, 300, graphHeight, 0);
            applet.ellipse(x, y, 6, 6);
        }

        // Draw direction map
        applet.translate(0, graphHeight + 10);
        applet.fill(0);
        applet.rect(0, 0, graphWidth, graphHeight);

        // draw vmap markers
        for (RealtimeInfo info : carStateSafe) {
            float x = graphWidth * info.getSplinePosition();
            float y = PApplet.map(info.getYaw(), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
            dirMapGraph.beginDraw();
            dirMapGraph.point(x, y);
            dirMapGraph.endDraw();
        }

        applet.image(dirMapGraph, 0, 0);

        // draw current dirMap
        applet.stroke(100, 255, 100);
        applet.noFill();
        applet.beginShape();
        for (int i = 0; i < dirMap.size(); i++) {
            float x = graphWidth / dirMap.size() * i;
            float y = PApplet.map(dirMap.get(i), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
            applet.vertex(x, y);
        }
        applet.endShape();

        // draw saved dirMap.
        applet.stroke(100, 100, 255);
        applet.beginShape();
        for (int i = 0; i < savedDirMap.size(); i++) {
            float x = graphWidth / savedDirMap.size() * i;
            float y = PApplet.map(savedDirMap.get(i), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
            applet.vertex(x, y);
        }
        applet.endShape();
        applet.noStroke();

        // draw car positions
        applet.fill(COLOR_WHITE);
        for (RealtimeInfo info : carStateSafe) {
            float x = graphWidth * info.getSplinePosition();
            float y = PApplet.map(info.getYaw(), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
            applet.ellipse(x, y, 6, 6);
        }

        applet.translate(-10, -LINE_HEIGHT - graphHeight - 10);
    }

}
