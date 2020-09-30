/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.logging;

import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPContainer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LoggingPanel extends LPContainer {

    private LoggingExtension extension;

    private int scroll;

    public LoggingPanel(LoggingExtension extension) {
        this.extension = extension;

        setName("LOGGING");
    }

    @Override
    public void draw() {
        int lineHeight = LookAndFeel.get().LINE_HEIGHT;
        int tabSize = 130;

        List<String> messages = new LinkedList<>();
        messages.addAll(extension.getMessages());
        Collections.reverse(messages);

        if (scroll < 0) {
            scroll = 0;
        }
        int visibleLines = applet.height / lineHeight;
        if (scroll > messages.size() - visibleLines) {
            scroll = messages.size() - visibleLines;
        }

        int n = 0;
        int scrollCount = scroll;
        for (String msg : messages) {
            if (scrollCount-- > 0) {
                continue;
            }

            int y = lineHeight * n++;

            applet.fill((n % 2 == 0) ? 50 : 40);
            applet.noStroke();
            applet.rect(0, y, applet.width, lineHeight);

            applet.fill(255);
            applet.textAlign(LEFT, CENTER);

            float x = 40;
            String[] partials = msg.split("\t");
            for (String partial : partials) {
                applet.text(partial, x, y + lineHeight / 2);
                float msgWidth = applet.textWidth(partial);
                x += (msgWidth - (msgWidth % tabSize) + tabSize);
            }
        }
        LookAndFeel.drawScrollBar(applet, messages.size(), visibleLines, scroll);
    }

    /*
    @Override
    public void mouseWheel(int count) {
        scroll += count;
        applet.forceRedraw();
    }
     */
}
