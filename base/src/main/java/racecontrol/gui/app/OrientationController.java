/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import processing.core.PApplet;
import static processing.core.PApplet.map;
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

    private Map<Integer, PVector> positions = new HashMap<>();
    private float min_x;
    private float max_x;
    private float min_y;
    private float max_y;
    private float min_z;
    private float max_z;
    private int focusedId = 0;

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
                PVector pos = new PVector(info.getWorldPositionX(), -info.getWorldPositionY(), info.getHeading());
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
                positions.put(info.getCarId(), pos);

            }
        } else if (e instanceof RealtimeUpdateEvent) {
            synchronized (positions) {
                positions.clear();
                focusedId = ((RealtimeUpdateEvent)e).getSessionInfo().getFocusedCarIndex();
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
            for (Entry<Integer, PVector> entry : positions.entrySet()) {
                PVector pos = entry.getValue();
                int id = entry.getKey();
                if(id == focusedId){
                    applet.fill(0, 255, 255);
                }else{
                    applet.fill(255, 255, 0);
                }
                float x = map(pos.x, min_x, max_x, 20, getWidth() - 20);
                float y = map(pos.y, min_y, max_y, 20, getHeight() - 20);
                //float z = map(pos.z, min_z, max_z, 1, 50);
                applet.noStroke();
                applet.ellipse(x, y, 10, 10);
                
                applet.stroke(255);
                applet.line(x, y, x+(float)Math.cos(pos.z)*20, y+(float)Math.sin(pos.z)*20);
            }
        }

    }
}
