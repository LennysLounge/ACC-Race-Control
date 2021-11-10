/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.dangerdetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import processing.core.PApplet;
import processing.core.PVector;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.CustomPApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class DangerDetectionController
        extends LPContainer
        implements EventListener, PageController {

    /**
     * Menu item for the page menu.
     */
    private final MenuItem menuItem;
    /**
     * Current track data.
     */
    private TrackData trackData;
    /**
     * Maps car ids to a list of their previous velocities.
     */
    private final Map<Integer, List<RealtimeInfo>> carV = new HashMap<>();

    private int focusedCarId = 0;

    public DangerDetectionController() {
        EventBus.register(this);
        menuItem = new Menu.MenuItem("Danger det.",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Debugging.png"));
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackDataEvent) {
            trackData = ((TrackDataEvent) e).getTrackData();
        } else if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            focusedCarId = ((RealtimeUpdateEvent) e).getSessionInfo().getFocusedCarIndex();
        }
    }

    private void onRealtimeCarUpdate(RealtimeInfo info) {
        synchronized (carV) {
            if (!carV.containsKey(info.getCarId())) {
                carV.put(info.getCarId(), new ArrayList<>());
            }
            var list = carV.get(info.getCarId());
            list.add(info);
            while (list.size() > 100) {
                list.remove(0);
            }
        }
        invalidate();
    }

    @Override
    public LPContainer getPanel() {
        return this;
    }

    @Override
    public Menu.MenuItem getMenuItem() {
        return menuItem;
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        if (trackData == null) {
            return;
        }

        float dWidth = (getWidth() - 30) / 2;
        float dHeight = (getHeight() - 30) / 2;

        applet.translate(10, 10);
        drawVMap(applet, dWidth, dHeight);

        applet.translate(dWidth + 10, 0);
        drawVMapDiff(applet, dWidth, dHeight);

        applet.translate(-dWidth - 10, dHeight + 10);
        //drawDMap(applet, dWidth, dHeight);
        drawDMapDiff(applet, dWidth * 2 + 10, dHeight);

        applet.translate(-dWidth - 20, -dHeight - 20);

    }

    private void drawVMap(PApplet applet, float w, float h) {
        applet.fill(0);
        applet.rect(0, 0, w, h);

        float speedScale = h / 300;

        // draw map line
        applet.stroke(100, 255, 100);
        applet.strokeWeight(2);
        applet.noFill();
        applet.beginShape();
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h - v * speedScale;
            applet.vertex(x, y);
        }
        applet.endShape();
        applet.fill(255, 100, 100, 100);
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, h);
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h - v * speedScale + 50 * speedScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, h);
        applet.endShape();

        // draw cars.
        applet.strokeWeight(3);
        synchronized (carV) {
            for (int carId : carV.keySet()) {
                applet.noFill();
                applet.stroke(100, 100, 255);
                applet.beginShape();
                float prevSplinePos = carV.get(carId).get(0).getSplinePosition();
                float x = 0;
                float y = 0;
                for (RealtimeInfo info : carV.get(carId)) {
                    if (prevSplinePos - info.getSplinePosition() > 0.5) {
                        applet.endShape();
                        applet.beginShape();
                    }
                    prevSplinePos = info.getSplinePosition();
                    x = info.getSplinePosition() * w;
                    y = h - info.getKMH() * speedScale;
                    applet.vertex(x, y);
                }
                applet.endShape();
                applet.fill(100, 100, 100);
                applet.noStroke();
                if (carId == focusedCarId) {
                    applet.fill(255);
                }
                applet.ellipse(x, y, 10, 10);
            }
        }
        applet.noStroke();
        applet.strokeWeight(1);
    }

    private void drawVMapDiff(PApplet applet, float w, float h) {
        applet.fill(0);
        applet.rect(0, 0, w, h);

        float baseLine = h / 3;
        float speedScale = h / 300;

        applet.stroke(100, 255, 100);
        applet.strokeWeight(2);
        applet.line(0, baseLine, w, baseLine);

        applet.fill(255, 100, 100, 100);
        applet.noStroke();
        applet.strokeWeight(2);
        applet.rect(0, baseLine + 50 * speedScale, w, h - baseLine - 50 * speedScale);

        // draw cars.
        applet.strokeWeight(3);
        synchronized (carV) {
            for (int carId : carV.keySet()) {
                applet.noFill();
                applet.stroke(100, 100, 255);
                applet.beginShape();
                float prevSplinePos = carV.get(carId).get(0).getSplinePosition();
                float x = 0;
                float y = 0;
                for (RealtimeInfo info : carV.get(carId)) {
                    if (prevSplinePos - info.getSplinePosition() > 0.5) {
                        applet.endShape();
                        applet.beginShape();
                    }
                    float v = info.getKMH() - getVMapValue(info.getSplinePosition());
                    prevSplinePos = info.getSplinePosition();
                    x = info.getSplinePosition() * w;
                    y = baseLine - v * speedScale;
                    applet.vertex(x, y);
                }
                applet.endShape();
                applet.fill(100, 100, 255);
                applet.noStroke();
                if (carId == focusedCarId) {
                    applet.fill(255);
                }
                applet.ellipse(x, y, 10, 10);
            }
        }
        applet.noStroke();
        applet.strokeWeight(1);

    }

    private void drawDMap(PApplet applet, float w, float h) {
        applet.fill(0);
        applet.rect(0, 0, w, h);

        // draw d map
        applet.stroke(100, 255, 100);
        applet.strokeWeight(2);
        applet.noFill();
        applet.beginShape();
        for (int i = 0; i < trackData.getDirectionMap().size(); i++) {
            float v = trackData.getDirectionMap().get(i);
            float x = (i * 1f / (trackData.getDirectionMap().size() - 1) * 1f) * w;
            float y = h / 2f + v * h / 2 / 3.5f;
            applet.vertex(x, y);
        }
        applet.endShape();

        // draw cars
        applet.strokeWeight(3);
        synchronized (carV) {
            for (int carId : carV.keySet()) {
                applet.noFill();
                applet.stroke(100, 100, 255);
                applet.beginShape();
                float prevSplinePos = carV.get(carId).get(0).getSplinePosition();
                for (RealtimeInfo info : carV.get(carId)) {
                    if (prevSplinePos - info.getSplinePosition() > 0.5) {
                        applet.endShape();
                        applet.beginShape();
                    }
                    prevSplinePos = info.getSplinePosition();
                    float x = info.getSplinePosition() * w;
                    float y = h / 2f + info.getYaw() * h / 2 / 3.5f;
                    applet.vertex(x, y);
                }
                applet.endShape();

                RealtimeInfo last = carV.get(carId).get(carV.get(carId).size() - 1);
                float x = last.getSplinePosition() * w;
                float y = h / 2f + last.getYaw() * h / 2 / 3.5f;
                applet.fill(100, 100, 255);
                applet.noStroke();
                if (carId == focusedCarId) {
                    applet.fill(255);
                }
                applet.ellipse(x, y, 10, 10);
            }
        }

        applet.noStroke();
        applet.strokeWeight(1);
    }

    private void drawDMapDiff(PApplet applet, float w, float h) {
        applet.fill(0);
        applet.rect(0, 0, w, h);

        float angleScale = h / 1f / 3.5f;
        float d = trackData.getTrackMeters() * 0.6f;

        // draw d map
        applet.stroke(100, 255, 100);
        applet.strokeWeight(2);
        applet.line(0, h / 2, w, h / 2);
        applet.line(0, h / 2 + (float) Math.PI / 2f * angleScale, w, h / 2 + (float) Math.PI / 2f * angleScale);
        applet.line(0, h / 2 - (float) Math.PI / 2f * angleScale, w, h / 2 - (float) Math.PI / 2f * angleScale);

        applet.stroke(255);

        applet.stroke(255);
        applet.beginShape();
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float z = 1 - (v - 50) / 200;
            z = z * z;
            z = 0.2f + Math.max(0, Math.min(1, z));
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f - z * angleScale;
            applet.vertex(x, y);
        }
        applet.endShape();
        applet.beginShape();
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float z = 1 - (v - 50) / 200;
            z = z * z;
            z = 0.2f + Math.max(0, Math.min(1, z));
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f + z * angleScale;
            applet.vertex(x, y);
        }
        applet.endShape();

 /*
        applet.beginShape();
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f - v;
            applet.vertex(x, y);
        }
        applet.endShape();
         */
 /*
        applet.fill(255, 100, 100, 100);
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, 0);
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float maxC = d / (v * v) * 2 + 0.1f;
            if (maxC > Math.PI / 2f) {
                maxC = (float) Math.PI / 2f;
            }
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f - maxC * angleScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, 0);
        applet.endShape();
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, h);
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float v = trackData.getGt3VelocityMap().get(i);
            float maxC = d / (v * v) * 2 + 0.1f;
            if (maxC > Math.PI / 2f) {
                maxC = (float) Math.PI / 2f;
            }
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f + maxC * angleScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, h);
        applet.endShape();
         */
        // draw cars
        applet.strokeWeight(2);
        synchronized (carV) {
            for (int carId : carV.keySet()) {
                applet.noFill();
                applet.stroke(100, 100, 255);
                applet.beginShape();
                float prevSplinePos = carV.get(carId).get(0).getSplinePosition();
                float x = 0;
                float y = 0;
                float maxC = 0;
                float dif = 0;
                for (RealtimeInfo info : carV.get(carId)) {
                    if (prevSplinePos - info.getSplinePosition() > 0.5) {
                        applet.endShape();
                        applet.beginShape();
                    }
                    prevSplinePos = info.getSplinePosition();
                    float current = info.getYaw();
                    float map = getDMapValue(info.getSplinePosition());
                    dif = angleBetewen(current, map);

                    float v = getVMapValue(info.getSplinePosition());
                    maxC = d / (v * v) * 2 + 0.1f;

                    x = info.getSplinePosition() * w;
                    y = h / 2f + dif * angleScale;
                    applet.vertex(x, y);
                }
                applet.endShape();
                applet.fill(255);
                applet.noStroke();
                if (carId == focusedCarId) {
                    applet.fill(255, 0, 0);
                }
                if (Math.abs(dif) > maxC) {
                    applet.fill(255, 255, 0);
                }
                applet.ellipse(x, y, 10, 10);
            }
        }

        applet.noStroke();

        applet.strokeWeight(
                1);
    }

    private float getVMapValue(float splinePos) {
        int size = trackData.getGt3VelocityMap().size();
        int lowerIndex = (int) Math.floor(splinePos * size) % size;
        int upperIndex = (lowerIndex + 1) % size;

        float t = (splinePos * size) % 1;
        float lower = trackData.getGt3VelocityMap().get(lowerIndex);
        float upper = trackData.getGt3VelocityMap().get(upperIndex);
        return lower * (1 - t) + upper * t;
    }

    private float getDMapValue(float splinePos) {
        int size = trackData.getDirectionMap().size();
        int lowerIndex = (int) Math.floor(splinePos * size) % size;
        if (lowerIndex < 0) {
            lowerIndex = trackData.getDirectionMap().size() - 1;
        }
        int upperIndex = (lowerIndex + 1) % size;

        float t = (splinePos * size) % 1;
        float lowerV = trackData.getDirectionMap().get(lowerIndex);
        float upperV = trackData.getDirectionMap().get(upperIndex);
        PVector lower = PVector.fromAngle(lowerV).mult(1 - t);
        PVector upper = PVector.fromAngle(upperV).mult(t);

        return lower.add(upper).heading();
    }

    private float angleBetewen(float a1, float a2) {
        float diff = a1 - a2;
        if (diff < -Math.PI) {
            diff += Math.PI * 2;
        }
        if (diff > Math.PI) {
            diff -= Math.PI * 2;
        }
        return diff;
    }

}
