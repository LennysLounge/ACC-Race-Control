/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static racecontrol.gui.LookAndFeel.COLOR_BLACK;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class CameraDefsPanel
        extends LPContainer {

    protected final LPLabel currentCamSet = new LPLabel("Camset");
    protected final LPLabel currentCam = new LPLabel("Cam");

    protected Map<String, Map<String, List<Float>>> camChanges = new HashMap<>();
    protected Map<String, List<String>> camSets = new HashMap<>();

    protected float currentSplinePos = 0;

    public CameraDefsPanel() {
        setName("Camera Defs");

        currentCamSet.setPosition(20, 0);
        addComponent(currentCamSet);
        currentCam.setPosition(120, 0);
        addComponent(currentCam);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.translate(20, LINE_HEIGHT + 20);
        drawCamGraph(applet, "set1");
        applet.translate(0, 220);
        drawCamGraph(applet, "set2");
        applet.translate(-20, -LINE_HEIGHT - 20 - 220);

    }

    private void drawCamGraph(PApplet applet, String camSet) {
        float w = getWidth() - 40;
        float h = 200;
        applet.fill(COLOR_BLACK);
        applet.rect(0, 0, w, h);
        if (camSets.containsKey(camSet)) {
            applet.fill(COLOR_WHITE);
            applet.textAlign(LEFT, CENTER);
            applet.textSize(TEXT_SIZE * 0.6f);

            for (int i = 0; i < camSets.get(camSet).size(); i++) {
                applet.fill(COLOR_WHITE);
                applet.noStroke();
                String cam = camSets.get(camSet).get(i);
                float x = (w - 40) / camSets.get(camSet).size() * i;
                applet.text(cam, x + 40, LINE_HEIGHT * (i % 2 == 0 ? 0.75f : 0.25f));

                applet.stroke(COLOR_WHITE);
                applet.noFill();
                int c = 0;
                for (Float changePos : camChanges.get(camSet).get(cam)) {
                    x = PApplet.map(changePos, 0, 1, 40, w);
                    applet.ellipse(x, LINE_HEIGHT + c * 5, 5, 5);
                    c++;
                }
            }
            float x = PApplet.map(currentSplinePos, 0, 1, 40, w);
            applet.stroke(COLOR_WHITE);
            applet.line(x, 0, x, h);
        }
    }

}
