/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import java.io.IOException;
import processing.event.KeyEvent;

/**
 *
 * @author Leonard
 */
public class LPNumberTextField
        extends LPTextField {

    @Override
    public void setValue(String text) {
        // do nothing
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (!isFocused()) {
            return;
        }
        if (!isEnabled()) {
            return;
        }

        if (event.getKeyCode() == 86 && (event.getModifiers() & KeyEvent.CTRL) > 0) {
            //KeyCode 86 == 'v'
            //past from clipboard
            String clipboard = getClipBoard().replaceAll("\n", "");
            try {
                Integer.parseInt(clipboard);
            } catch (NumberFormatException e) {
                // clipboard does not contain a number
                // do not paste, ignore keyboard event.
                return;
            }
        } else if (isPrintableChar(event.getKey())) {
            if (!Character.isDigit(event.getKey())) {
                //not a digit, ignore keyboard event.
                return;
            }
        }
        super.onKeyPressed(event);
    }

    public void setValue(int value) {
        super.setValue(String.valueOf(value));
    }

    public int getNumber() {
        return Integer.parseInt(getValue());
    }

    private String getClipBoard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (HeadlessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedFlavorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c))
                && c != CHAR_UNDEFINED
                && block != null
                && block != Character.UnicodeBlock.SPECIALS;
    }

}
