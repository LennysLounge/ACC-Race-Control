/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.logging;

import ACCLiveTiming.visualisation.gui.LPTable;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LogMessage extends LPTable.Entry {

    private String message;
    public static LPTable.Renderer logRenderer = new LPTable.Renderer() {
        @Override
        public void draw(LPTable.Entry entry) {
            applet.fill(isOdd ? 40 : 50);
            applet.rect(0, 0, width, height);

            String message = ((LogMessage) entry).getMessage();
            applet.fill(255);
            applet.textAlign(LEFT, CENTER);
            float x = 10;
            int tabSize = 140;
            String[] partials = message.split("\t");
            for (String partial : partials) {
                applet.text(partial, x, height / 2f);
                float msgWidth = applet.textWidth(partial);
                x += (msgWidth - (msgWidth % tabSize) + tabSize);
            }
        }
    };

    public LogMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
