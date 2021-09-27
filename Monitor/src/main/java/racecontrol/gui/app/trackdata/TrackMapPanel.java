/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CLOSE;
import processing.core.PVector;
import racecontrol.client.extension.trackdata.TrackData;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class TrackMapPanel
        extends LPContainer {

    protected TrackData trackData;

    public TrackMapPanel() {
        setName("Trackmap");
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.fill(0);
        applet.rect(10, 10, getWidth() - 20, getHeight() - 20);

        if (trackData == null) {
            return;
        }

        List<PVector> trackMap = new ArrayList<>();
        PVector pos = new PVector(0, 0);
        trackMap.add(new PVector(pos.x, pos.y));
        trackData.getDirectionMap().forEach(dir -> {
            pos.add(PVector.fromAngle(dir));
            trackMap.add(new PVector(pos.x, pos.y));
        });

        PVector topLeft = new PVector();
        PVector bottomRight = new PVector();
        trackMap.forEach(vec -> {
            topLeft.y = Math.min(topLeft.y, vec.y);
            topLeft.x = Math.min(topLeft.x, vec.x);
            bottomRight.y = Math.max(bottomRight.y, vec.y);
            bottomRight.x = Math.max(bottomRight.x, vec.x);

        });

        float mapHeight = bottomRight.y - topLeft.y;
        float mapWidth = bottomRight.x - topLeft.x;
        float targetHeight = getHeight() - 40;
        float targetWidth = getWidth() - 40;
        float scale = Math.min(targetHeight / mapHeight, targetWidth / mapWidth);

        float xOffset = (targetWidth - mapWidth * scale) / 2f;
        float yOffset = (targetHeight - mapHeight * scale) / 2f;

        //move and scale map points
        trackMap.forEach(vec -> {
            vec.sub(topLeft);
            vec.mult(scale);
            vec.add(new PVector(xOffset, yOffset));
        });

        applet.noFill();
        applet.stroke(255);
        applet.strokeWeight(3);
        applet.beginShape();
        trackMap.forEach(vec -> applet.curveVertex(vec.x + 20, vec.y + 20));
        applet.endShape(CLOSE);
        applet.noStroke();
        applet.strokeWeight(1);
    }
}
