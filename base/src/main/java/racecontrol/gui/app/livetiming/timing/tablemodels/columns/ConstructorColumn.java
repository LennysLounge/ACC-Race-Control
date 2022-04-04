/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import java.util.HashMap;
import java.util.Map;
import processing.core.PApplet;
import processing.core.PImage;
import racecontrol.client.protocol.enums.CarModel;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.model.Car;
import static racecontrol.gui.LookAndFeel.COLOR_MEDIUM_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.RaceControlApplet.getApplet;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class ConstructorColumn
        extends LPTableColumn {

    private static PImage image = getApplet().loadResourceAsPImage(
            "/images/RC_Menu_Symbol.png");

    private final Map<CarModel, PImage> constructorImages = new HashMap<>();

    public ConstructorColumn() {
        super("");
        setMinWidth(LINE_HEIGHT * 1f);
        setMaxWidth(LINE_HEIGHT * 1f);
        setPriority(1000);
        setCellRenderer(this::constructorRenderer);
    }

    protected void constructorRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        
        CarModel model = car.carModel;
        if (!constructorImages.containsKey(model)) {
            if (model == CarModel.ERROR) {
                constructorImages.put(model, null);
            }
            constructorImages.put(model, getApplet().loadResourceAsPImage(
                    "/images/constructors/" + model.getConstructor() + ".png"));
        }

        applet.fill(COLOR_MEDIUM_GRAY);
        applet.rect(0, 1, context.width, context.height - 2);

        PImage i = constructorImages.get(model);
        if (i == null) {
            return;
        }

        float size = Math.min(context.width, context.height - 2) * 0.8f;

        applet.tint(255);
        applet.image(i, context.width / 2f - size / 2f, context.height / 2f - size / 2f,
                size, size);
    }

}
