/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.client.ExtensionPanel;
import ACCLiveTiming.networking.enums.SessionType;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.utility.VisualUtils;
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
    public void drawPanel(PGraphics context) {
        int lineHeight = VisualUtils.LINE_HEIGHT;

        List<Accident> accidents = new LinkedList<>();
        accidents.addAll(extension.getAccidents());
        Collections.reverse(accidents);

        if (scroll < 0) {
            scroll = 0;
        }
        int visibleLines = context.height / lineHeight;
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

            context.fill((n % 2 == 0) ? 50 : 40);
            context.stroke((n % 2 == 0) ? 50 : 40);

            context.rect(0, y, context.width, lineHeight);

            if (accident.getSessionID().getType() == SessionType.PRACTICE) {
                context.fill(VisualUtils.COLOR_PRACTICE);
            } else if (accident.getSessionID().getType() == SessionType.QUALIFYING) {
                context.fill(VisualUtils.COLOR_QUALIFYING);
            } else if (accident.getSessionID().getType() == SessionType.RACE) {
                context.fill(VisualUtils.COLOR_RACE);
            }

            context.rect(x, y, lineHeight, lineHeight);
            context.fill(255);
            context.textAlign(CENTER, CENTER);
            VisualUtils.text(context, "" + accident.getIncidentNumber(), x + lineHeight / 2, y + lineHeight / 2);
            context.textAlign(LEFT, CENTER);
            VisualUtils.text(context, TimeUtils.asDuration(accident.getEarliestTime()),
                    x + lineHeight + 20, y + lineHeight / 2);

            int m = 0;
            for (int carId : accident.getCars()) {
                int xx = (int) (lineHeight * 1.5f * m++);
                context.fill(VisualUtils.COLOR_RED);
                context.rect(300 + xx, y, lineHeight * 1.5f, lineHeight);
                context.fill(255);
                context.textAlign(CENTER, CENTER);
                VisualUtils.text(context, "" + extension.getModel().getCar(carId).getCarNumber(),
                        300 + xx + lineHeight * 1.5f / 2, y + lineHeight / 2);
            }
        }
        VisualUtils.drawScrollBar(context, accidents.size(), visibleLines, scroll);
    }

    @Override
    public void mouseWheel(int count) {
        scroll += count;
    }
}
