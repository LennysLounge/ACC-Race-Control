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
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.gui.lpui.LPComponent;

/**
 *
 * @author Leonard
 */
public class ChangeLogPanel
        extends LPComponent {

    private final List<MarkdownEntry> changeLog = new ArrayList<>();

    public ChangeLogPanel() {
        loadChangelog();
    }

    private void loadChangelog() {
        InputStream in = ChangeLogPanel.class.getResourceAsStream("/ChangeLog.md");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        changeLog.addAll(reader.lines()
                .map(line -> new MarkdownEntry(line))
                .collect(Collectors.toList())
        );
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        int y = 0;
        for (MarkdownEntry line : changeLog) {
            line.render(applet, 10, y);
            y += line.getHeight();
        }
    }

    @Override
    public void setSize(float w, float h) {
        super.setSize(w, h);
        changeLog.forEach(entry -> entry.setWidth(w - 20));

        calculateHeight();
    }

    private void calculateHeight() {
        float height = 4000;
        height = changeLog.stream()
                .map(entry -> entry.getHeight())
                .reduce(0f, Float::sum);
        super.setSize(getWidth(), height);
    }

}
