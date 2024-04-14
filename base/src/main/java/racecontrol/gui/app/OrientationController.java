/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.map;
import static processing.core.PApplet.max;
import processing.core.PVector;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class OrientationController
        extends LPContainer
        implements PageController, EventListener {

    private final Menu.MenuItem menuItem;

    private List<PVector> positions = new ArrayList<>();
    private float min_x;
    private float max_x;
    private float min_y;
    private float max_y;
    private float min_z;
    private float max_z;

    public OrientationController() {
        EventBus.register(this);
        menuItem = new Menu.MenuItem("Orientation", null);
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
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            RealtimeInfo info = ((RealtimeCarUpdateEvent) e).getInfo();
            synchronized (positions) {
                PVector pos = new PVector(info.getRoll(), -info.getYaw(), info.getPitch());
                if (pos.x < min_x) {
                    min_x = pos.x;
                }
                if (pos.y < min_y) {
                    min_y = pos.y;
                }
                if (pos.x > max_x) {
                    max_x = pos.x;
                }
                if (pos.y > max_y) {
                    max_y = pos.y;
                }
                if (pos.z > max_z) {
                    max_z = pos.z;
                }
                if (pos.z < min_z) {
                    min_z = pos.z;
                }
                positions.add(pos);

            }
        } else if (e instanceof RealtimeUpdateEvent) {
            synchronized (positions) {
                positions.clear();
            }
            invalidate();
        }
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(255, 255, 0);
        applet.noStroke();
        synchronized (positions) {
            for (PVector pos : positions) {
                float x = map(pos.x, min_x, max_x, 20, getWidth() - 20);
                float y = map(pos.y, min_y, max_y, 20, getHeight() - 20);
                float z = map(pos.z, min_z, max_z, 1, 50);
                applet.ellipse(x, y, z, z);
            }
        }

    }
}
