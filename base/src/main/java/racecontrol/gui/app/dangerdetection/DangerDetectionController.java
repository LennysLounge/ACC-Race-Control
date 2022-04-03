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
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.dangerdetection.DangerDetectionExtension;
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
     * Reference to the extension.
     */
    private final DangerDetectionExtension extension;
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
    /**
     * The car that is currently in focus.
     */
    private int focusedCarId = 0;

    public DangerDetectionController() {
        EventBus.register(this);
        extension = DangerDetectionExtension.getInstance();
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

        float dWidth = getWidth() - 20;
        float dHeight = (getHeight() - 30) / 2;

        applet.translate(10, 10);
        drawVMap(applet, dWidth, dHeight);

        applet.translate(0, dHeight + 10);
        drawDMapDiff(applet, dWidth, dHeight);

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

        // draw white area
        applet.fill(100);
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, h);
        List<Float> toleranceMapW = extension.getVelocityToleranceWhiteMap();
        for (int i = 0; i < toleranceMapW.size(); i++) {
            float v = toleranceMapW.get(i);
            float x = (i * 1f / (toleranceMapW.size() - 1) * 1f) * w;
            float y = h - v * speedScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, h);
        applet.endShape();

        // draw yellow area
        applet.fill(150, 150, 0);
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, h);
        List<Float> toleranceMapY = extension.getVelocityToleranceYellowMap();
        for (int i = 0; i < toleranceMapY.size(); i++) {
            float v = toleranceMapY.get(i);
            float x = (i * 1f / (toleranceMapY.size() - 1) * 1f) * w;
            float y = h - v * speedScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, h);
        applet.endShape();

        // draw cars.
        applet.strokeWeight(2);
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
                    applet.vertex(info.getSplinePosition() * w,
                            h - info.getKMH() * speedScale);
                }
                applet.endShape();
            }
            applet.noStroke();
            for (int carId : carV.keySet()) {
                var info = carV.get(carId).get(carV.get(carId).size() - 1);
                applet.fill(200);
                applet.noStroke();
                if (focusedCarId == carId) {
                    applet.fill(255, 0, 0);
                    applet.stroke(255, 0, 0);
                }
                if (extension.isCarWhiteFlag(carId)) {
                    applet.fill(255);
                }
                if (extension.isCarYellowFlag(carId)) {
                    applet.fill(255, 255, 0);
                }
                applet.ellipse(info.getSplinePosition() * w,
                        h - info.getKMH() * speedScale, 15, 15);
            }
        }

        applet.noStroke();
        applet.strokeWeight(1);
    }

    private void drawDMapDiff(PApplet applet, float w, float h) {
        applet.fill(0);
        applet.rect(0, 0, w, h);

        float angleScale = h / 1f / 3.5f;

        // draw d map
        applet.stroke(100, 255, 100);
        applet.strokeWeight(2);
        applet.line(0, h / 2, w, h / 2);
        applet.line(0, h / 2 + (float) Math.PI / 2f * angleScale, w, h / 2 + (float) Math.PI / 2f * angleScale);
        applet.line(0, h / 2 - (float) Math.PI / 2f * angleScale, w, h / 2 - (float) Math.PI / 2f * angleScale);

        List<Float> toleranceMap = extension.getDirectionToleranceMap();
        applet.fill(150, 150, 0);
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, 0);
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f - toleranceMap.get(i) * angleScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, 0);
        applet.endShape();
        applet.noStroke();
        applet.beginShape();
        applet.vertex(0, h);
        for (int i = 0; i < trackData.getGt3VelocityMap().size(); i++) {
            float x = (i * 1f / (trackData.getGt3VelocityMap().size() - 1) * 1f) * w;
            float y = h / 2f + toleranceMap.get(i) * angleScale;
            applet.vertex(x, y);
        }
        applet.vertex(w, h);
        applet.endShape();

        // draw cars
        applet.strokeWeight(2);
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
                    float current = info.getYaw();
                    float map = getDMapValue(info.getSplinePosition());
                    applet.vertex(info.getSplinePosition() * w,
                            h / 2f + angleBetewen(current, map) * angleScale);
                }
                applet.endShape();
            }
            applet.noStroke();
            for (int carId : carV.keySet()) {
                applet.fill(200);
                applet.noStroke();
                if (focusedCarId == carId) {
                    applet.fill(255, 0, 0);
                    applet.stroke(255, 0, 0);
                }
                if (extension.isCarWhiteFlag(carId)) {
                    applet.fill(255);
                }
                if (extension.isCarYellowFlag(carId)) {
                    applet.fill(255, 255, 0);
                }
                var info = carV.get(carId).get(carV.get(carId).size() - 1);
                float current = info.getYaw();
                float map = getDMapValue(info.getSplinePosition());
                applet.ellipse(info.getSplinePosition() * w,
                        h / 2f + angleBetewen(current, map) * angleScale,
                        15, 15);
            }
        }
        applet.noStroke();
        applet.strokeWeight(1);
    }

    private float getVMapValue(float splinePos) {
        if (trackData.getGt3VelocityMap().isEmpty()) {
            return 0;
        }

        int size = trackData.getGt3VelocityMap().size();
        int lowerIndex = (int) Math.floor(splinePos * size) % size;
        int upperIndex = (lowerIndex + 1) % size;

        float t = (splinePos * size) % 1;
        float lower = trackData.getGt3VelocityMap().get(lowerIndex);
        float upper = trackData.getGt3VelocityMap().get(upperIndex);
        return lower * (1 - t) + upper * t;
    }

    private float getDMapValue(float splinePos) {
        if (trackData.getDirectionMap().isEmpty()) {
            return 0;
        }

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
