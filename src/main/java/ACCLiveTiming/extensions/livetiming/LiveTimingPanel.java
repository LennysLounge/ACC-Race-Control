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
    public void drawPanel(PGraphics base) {
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
        int visibleLines = base.height / lineHeight;
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

            base.fill((n % 2 == 0) ? 50 : 40);
            base.stroke((n % 2 == 0) ? 50 : 40);
            base.rect(0, y, base.width, lineHeight);
            base.fill(LookAndFeel.get().COLOR_RED);
            base.rect(x, y, lineHeight, lineHeight);
            base.fill(255);
            base.rect(x + 400, y, lineHeight * 1.5f, lineHeight);

            base.noStroke();
            base.fill(255);
            base.textAlign(CENTER, CENTER);
            base.text(position, x + lineHeight / 2, y + lineHeight / 2);
            base.textAlign(LEFT, CENTER);
            base.text(name, x + lineHeight + 20, y + lineHeight / 2);
            base.fill(0);
            base.textAlign(CENTER, CENTER);
            base.text(carNumber, x + 400 + lineHeight * 1.5f / 2, y + lineHeight / 2);
            base.fill(255);
            base.textAlign(LEFT, CENTER);
            base.text(currentLap, x + 500, y + lineHeight / 2);
            base.text(delta, x + 650, y + lineHeight / 2);
        }
        LookAndFeel.drawScrollBar(base, cars.size(), visibleLines, scroll);
    }

    @Override
    public void mouseWheel(int count) {
        scroll += count;
    }

}
