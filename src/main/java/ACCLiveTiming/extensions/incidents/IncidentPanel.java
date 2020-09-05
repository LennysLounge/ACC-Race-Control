/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.extensions.ExtensionPanel;
import ACCLiveTiming.networking.enums.SessionType;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
public class IncidentPanel extends ExtensionPanel {

    private IncidentExtension extension;

    private int scroll;

    public IncidentPanel(IncidentExtension extension) {
        this.extension = extension;
        this.displayName = "INCIDENTS";
    }

    @Override
    public void drawPanel() {
        int lineHeight = LookAndFeel.get().LINE_HEIGHT;

        List<Accident> accidents = new LinkedList<>();
        accidents.addAll(extension.getAccidents());
        Collections.reverse(accidents);

        if (scroll < 0) {
            scroll = 0;
        }
        int visibleLines = layer.height / lineHeight;
        if (scroll > accidents.size() - visibleLines) {
            scroll = accidents.size() - visibleLines;
        }

        int n = 0;
        int scrollCount = scroll;
        for (Accident accident : accidents) {
            if (scrollCount-- > 0) {
                continue;
            }

            int y = lineHeight * n++;
            int x = 20;

            layer.fill((n % 2 == 0) ? 50 : 40);
            layer.stroke((n % 2 == 0) ? 50 : 40);

            layer.rect(0, y, layer.width, lineHeight);

            if (accident.getSessionID().getType() == SessionType.PRACTICE) {
                layer.fill(LookAndFeel.get().COLOR_PRACTICE);
            } else if (accident.getSessionID().getType() == SessionType.QUALIFYING) {
                layer.fill(LookAndFeel.get().COLOR_QUALIFYING);
            } else if (accident.getSessionID().getType() == SessionType.RACE) {
                layer.fill(LookAndFeel.get().COLOR_RACE);
            }

            layer.rect(x, y, lineHeight, lineHeight);
            layer.fill(255);
            layer.textAlign(CENTER, CENTER);
            layer.text("" + accident.getIncidentNumber(), x + lineHeight / 2, y + lineHeight / 2);
            layer.textAlign(LEFT, CENTER);
            layer.text(TimeUtils.asDuration(accident.getEarliestTime()),
                    x + lineHeight + 20, y + lineHeight / 2);

            int m = 0;
            for (int carId : accident.getCars()) {
                int xx = (int) (lineHeight * 1.5f * m++);
                layer.fill(LookAndFeel.get().COLOR_RED);
                layer.rect(300 + xx, y, lineHeight * 1.5f, lineHeight);
                layer.fill(255);
                layer.textAlign(CENTER, CENTER);
                layer.text("" + extension.getModel().getCar(carId).getCarNumber(),
                        300 + xx + lineHeight * 1.5f / 2, y + lineHeight / 2);
            }
        }
        LookAndFeel.drawScrollBar(layer, accidents.size(), visibleLines, scroll);
    }

    @Override
    public void mouseWheel(int count) {
        scroll += count;
    }
}
