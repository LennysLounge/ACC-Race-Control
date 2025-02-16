/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import processing.core.PGraphics;
import racecontrol.client.protocol.RealtimeInfo;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPCheckBox;
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

    private final LPLabel speedTrapLabel = new LPLabel("Speed trap");
    protected final LPTextField speedTrapTextField = new LPTextField();

    protected final LPButton saveToFileButton = new LPButton("Save to file");
    protected final LPButton saveButton = new LPButton("Apply");

    protected final LPCheckBox enableVMapCheckBox = new LPCheckBox();
    protected final LPCheckBox enableDMapCheckBox = new LPCheckBox();

    protected List<Float> vMap = new ArrayList<>();
    protected List<Float> savedVMap = new ArrayList<>();
    private PGraphics vMapGraph;

    protected List<Float> dirMap = new ArrayList<>();
    protected List<Float> savedDirMap = new ArrayList<>();
    private PGraphics dirMapGraph;

    private float graphHeight;
    private float graphWidth;

    protected float speedTrapLine;

    private List<RealtimeInfo> carStates = new ArrayList<>();

    public TrackDataPanel() {
        setName("Data");
        addComponent(trackNameLabel);

        addComponent(sectorOneLabel);
        sectorOneTextField.setSize(100, LINE_HEIGHT);
        addComponent(sectorOneTextField);
        addComponent(sectorTwoLabel);
        sectorTwoTextField.setSize(100, LINE_HEIGHT);
        addComponent(sectorTwoTextField);
        addComponent(sectorThreeLabel);
        sectorThreeTextField.setSize(100, LINE_HEIGHT);
        addComponent(sectorThreeTextField);

        addComponent(speedTrapLabel);
        speedTrapTextField.setSize(100, LINE_HEIGHT);
        addComponent(speedTrapTextField);

        saveToFileButton.setSize(100, LINE_HEIGHT);
        addComponent(saveToFileButton);
        saveButton.setSize(100, LINE_HEIGHT);
        addComponent(saveButton);

        addComponent(enableVMapCheckBox);
        addComponent(enableDMapCheckBox);
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

        speedTrapLabel.setPosition(770, 0);
        speedTrapTextField.setPosition(870, 0);

        saveToFileButton.setPosition(w - 110, 0);
        saveButton.setPosition(w - 220, 0);

        graphHeight = (getHeight() - LINE_HEIGHT - 20) / 2f;
        graphWidth = getWidth() - 20;

        enableVMapCheckBox.setPosition(20, LINE_HEIGHT + 10);
        enableDMapCheckBox.setPosition(20, LINE_HEIGHT + 20 + graphHeight);

        resizeGraphs();
    }

    private void resizeGraphs() {
        PGraphics g = getApplet().createGraphics((int) Math.max(100, graphWidth), (int) Math.max(100, graphHeight));
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
        g = getApplet().createGraphics((int) Math.max(100, graphWidth), (int) Math.max(100, graphHeight));
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
        if (carStates.size() > 300) {
            carStates.add(info);
        }
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
        if (enableVMapCheckBox.isSelected()) {
            applet.stroke(100, 255, 100);
            applet.noFill();
            applet.beginShape();
            for (int i = 0; i < vMap.size(); i++) {
                float x = graphWidth / vMap.size() * i;
                float y = PApplet.map(vMap.get(i), 0, 300, graphHeight, 0);
                applet.vertex(x, y);
            }
            applet.endShape();
        }

        // draw saved vmap.
        applet.stroke(100, 100, 255);
        applet.noFill();
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

        // draw speed trap line
        int xx = (int) (speedTrapLine * graphWidth);
        applet.stroke(255);
        applet.line(xx, 0, xx, graphHeight);
        applet.noStroke();

        // Draw direction map
        applet.translate(0, graphHeight + 10);
        applet.fill(0);
        applet.rect(0, 0, graphWidth, graphHeight);

        // draw vmap markers
        for (RealtimeInfo info : carStateSafe) {
            float x = graphWidth * info.getSplinePosition();
            float y = PApplet.map(info.getHeading(), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
            dirMapGraph.beginDraw();
            dirMapGraph.point(x, y);
            dirMapGraph.endDraw();
        }

        applet.image(dirMapGraph, 0, 0);

        // draw current dirMap
        if (enableDMapCheckBox.isSelected()) {
            applet.stroke(100, 255, 100);
            applet.noFill();
            applet.beginShape();
            for (int i = 0; i < dirMap.size(); i++) {
                float x = graphWidth / dirMap.size() * i;
                float y = PApplet.map(dirMap.get(i), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
                applet.vertex(x, y);
            }
            applet.endShape();
        }

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
            float y = PApplet.map(info.getHeading(), -PApplet.PI, PApplet.PI, graphHeight - 10, 10);
            applet.ellipse(x, y, 6, 6);
        }

        applet.translate(-10, -LINE_HEIGHT - graphHeight - 10);
    }

}
