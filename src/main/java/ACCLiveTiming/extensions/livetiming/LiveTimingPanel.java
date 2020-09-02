/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.client.ExtensionPanel;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.ArrayList;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
public class LiveTimingPanel extends ExtensionPanel {

    private LiveTimingExtension extension;

    private int scroll;

    public LiveTimingPanel(LiveTimingExtension extension) {
        this.extension = extension;

        this.displayName = "LIVE TIMING";
    }

    @Override
    public void drawPanel(PGraphics context) {
        int lineHeight = LookAndFeel.get().LINE_HEIGHT;

        //sort cars for position.
        ArrayList<CarInfo> cars = new ArrayList<>();
        cars.addAll(extension.getModel().getCarsInfo().values());
        for (int i = 0; i < cars.size(); i++) {
            for (int j = 0; j < cars.size() - 1; j++) {
                if (cars.get(j).getRealtime().getPosition() > cars.get(j + 1).getRealtime().getPosition()
                        || cars.get(j).getRealtime().getPosition() == 0) {
                    CarInfo tmp = cars.get(j);
                    cars.set(j, cars.get(j + 1));
                    cars.set(j + 1, tmp);
                }
            }
        }

        if (scroll < 0) {
            scroll = 0;
        }
        int visibleLines = context.height / lineHeight;
        if (scroll > cars.size() - visibleLines) {
            scroll = cars.size() - visibleLines;
        }

        int n = 0;
        int scrollCount = scroll;
        for (CarInfo car : cars) {
            if (scrollCount-- > 0) {
                continue;
            }

            String position = String.valueOf(car.getRealtime().getPosition());
            String name = car.getDriver().getFirstName() + ". " + car.getDriver().getLastName();
            String carNumber = String.valueOf(car.getCarNumber());
            String currentLap = TimeUtils.asLapTime(car.getRealtime().getCurrentLap().getLapTimeMS());
            String delta = TimeUtils.asDelta(car.getRealtime().getDelta());

            int y = lineHeight * n++;
            int x = 20;

            context.fill((n % 2 == 0) ? 50 : 40);
            context.stroke((n % 2 == 0) ? 50 : 40);
            context.rect(0, y, context.width, lineHeight);
            context.fill(LookAndFeel.get().COLOR_RED);
            context.rect(x, y, lineHeight, lineHeight);
            context.fill(255);
            context.rect(x + 400, y, lineHeight * 1.5f, lineHeight);

            context.noStroke();
            context.fill(255);
            context.textAlign(CENTER, CENTER);
            LookAndFeel.text(context, position, x + lineHeight / 2, y + lineHeight / 2);
            context.textAlign(LEFT, CENTER);
            LookAndFeel.text(context, name, x + lineHeight + 20, y + lineHeight / 2);
            context.fill(0);
            context.textAlign(CENTER, CENTER);
            LookAndFeel.text(context, carNumber, x + 400 + lineHeight * 1.5f / 2, y + lineHeight / 2);
            context.fill(255);
            context.textAlign(LEFT, CENTER);
            LookAndFeel.text(context, currentLap, x + 500, y + lineHeight / 2);
            LookAndFeel.text(context, delta, x + 650, y + lineHeight / 2);
        }
        LookAndFeel.drawScrollBar(context, cars.size(), visibleLines, scroll);
    }

    @Override
    public void mouseWheel(int count) {
        scroll += count;
    }

}
