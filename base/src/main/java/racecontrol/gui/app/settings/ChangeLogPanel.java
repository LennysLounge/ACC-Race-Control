/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.settings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPComponent;

/**
 *
 * @author Leonard
 */
public class ChangeLogPanel
        extends LPComponent {

    private final List<String> changeLog = new ArrayList<>();

    public ChangeLogPanel() {
        loadChangelog();
    }

    private void loadChangelog() {
        InputStream in = ChangeLogPanel.class.getResourceAsStream("/ChangeLog.md");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        changeLog.addAll(reader.lines().collect(Collectors.toList()));
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_RED);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(COLOR_WHITE);
        applet.noStroke();
        applet.textAlign(LEFT, CENTER);
        int y = 0;
        for (String line : changeLog) {
            applet.text(line, 0, y + LINE_HEIGHT / 2f);
            y += LINE_HEIGHT * 0.7f;
        }
    }

    @Override
    public void setSize(float w, float h) {
        super.setSize(w, h);
        calculateHeight();
    }

    private void calculateHeight() {
        int lines = changeLog.size();

        super.setSize(getWidth(), lines * LINE_HEIGHT * 0.7f);
    }

}
