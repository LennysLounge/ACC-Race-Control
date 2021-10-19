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

        // draw sector one lines
        int lower = (int) Math.floor(trackData.getSectorOneLine() * trackMap.size());
        int upper = ((lower + 1) % trackMap.size());
        float t = trackData.getSectorOneLine() * trackMap.size() % 1;
        PVector p = trackMap.get(lower).copy().mult(1 - t).add(
                trackMap.get(upper).copy().mult(t)
        );
        PVector dir = trackMap.get(upper).copy().sub(trackMap.get(lower)).normalize();
        dir = dir.set(-dir.y, dir.x);
        applet.fill(255);
        applet.line(p.x - dir.x * 15 + 20, p.y - dir.y * 15 + 20,
                p.x + dir.x * 15 + 20, p.y + dir.y * 15 + 20);
        applet.text("S1", p.x - dir.x * 30 + 20, p.y - dir.y * 30 + 20);

        // sector two
        lower = (int) Math.floor(trackData.getSectorTwoLine() * trackMap.size()) % trackMap.size();
        upper = ((lower + 1) % trackMap.size());
        t = trackData.getSectorTwoLine() * trackMap.size() % 1;
        p = trackMap.get(lower).copy().mult(1 - t).add(
                trackMap.get(upper).copy().mult(t)
        );
        dir = trackMap.get(upper).copy().sub(trackMap.get(lower)).normalize();
        dir = dir.set(-dir.y, dir.x);
        applet.fill(255);
        applet.line(p.x - dir.x * 15 + 20, p.y - dir.y * 15 + 20,
                p.x + dir.x * 15 + 20, p.y + dir.y * 15 + 20);
        applet.text("S2", p.x - dir.x * 30 + 20, p.y - dir.y * 30 + 20);

        // sector three
        lower = (int) Math.floor(trackData.getSectorThreeLine() * trackMap.size()) % trackMap.size();
        upper = ((lower + 1) % trackMap.size());
        t = trackData.getSectorThreeLine() * trackMap.size() % 1;
        p = trackMap.get(lower).copy().mult(1 - t).add(
                trackMap.get(upper).copy().mult(t)
        );
        dir = trackMap.get(upper).copy().sub(trackMap.get(lower)).normalize();
        dir = dir.set(-dir.y, dir.x);
        applet.fill(255);
        applet.line(p.x - dir.x * 15 + 20, p.y - dir.y * 15 + 20,
                p.x + dir.x * 15 + 20, p.y + dir.y * 15 + 20);
        applet.text("S3", p.x - dir.x * 30 + 20, p.y - dir.y * 30 + 20);

        // speed trap
        lower = (int) Math.floor(trackData.getSpeedTrapLine() * trackMap.size()) % trackMap.size();
        upper = ((lower + 1) % trackMap.size());
        t = trackData.getSpeedTrapLine() * trackMap.size() % 1;
        p = trackMap.get(lower).copy().mult(1 - t).add(
                trackMap.get(upper).copy().mult(t)
        );
        dir = trackMap.get(upper).copy().sub(trackMap.get(lower)).normalize();
        dir = dir.set(-dir.y, dir.x);
        applet.fill(255);
        applet.line(p.x - dir.x * 15 + 20, p.y - dir.y * 15 + 20,
                p.x + dir.x * 15 + 20, p.y + dir.y * 15 + 20);
        applet.text("Speed trap", p.x + dir.x * 30 + 20, p.y + dir.y * 30 + 20);

        applet.noStroke();
        applet.strokeWeight(1);
    }
}
