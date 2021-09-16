/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.lpgui.gui;

import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.LookAndFeel.COLOR_GRAY;
import static racecontrol.LookAndFeel.COLOR_MEDIUM_DARK_GRAY;
import static racecontrol.LookAndFeel.COLOR_RED;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import processing.core.PApplet;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TEXT;
import processing.event.KeyEvent;

/**
 *
 * @author Leonard
 */
public class LPTextField
        extends LPComponent {

    /**
     * The text value of this text field.
     */
    private String text = "";
    /**
     * True if the cursor is currently visible.
     */
    private boolean cursorBlinkOn = false;
    /**
     * Timer to make the curser blink.
     */
    private final Timer cursorBlinkTimer = new Timer();
    /**
     * Task that the timer uses to turnt the blink on and off.
     */
    private CursorBlinkTask cursorBlinkTask;
    /**
     * Padding the text field has on the left and right.
     */
    private final int padding = 10;
    /**
     * The text that is actually represented in the text field.
     */
    private String presentationText = "";
    /**
     * The number of characters the presentationText is offset from the full
     * text.
     */
    private int presentationTextOffset = 0;
    /**
     * Current cursor position in the presentationText.
     */
    private int cursorPosition;
    /**
     * The position in the text where the selection stars.
     */
    private int selectionStartIndex;
    /**
     * True if the mouse is currently pressed on this component.
     */
    private boolean isMouseDown;

    @Override
    public void draw(PApplet applet) {
        if (isEnabled()) {
            if (isFocused()) {
                applet.fill(COLOR_RED);
            } else if (isMouseOver()) {
                applet.fill(COLOR_DARK_RED);
            } else {
                applet.fill(COLOR_GRAY);
            }
        } else {
            applet.fill(COLOR_MEDIUM_DARK_GRAY);
        }

        applet.noStroke();
        applet.rect(0, 2, getWidth(), getHeight() - 4);

        applet.textFont(LookAndFeel.fontRegular());

        if (isSelectionActive() && isFocused() && isEnabled()) {
            int selectionStartInPresentationText = selectionStartIndex - presentationTextOffset;
            selectionStartInPresentationText = Math.max(0, Math.min(selectionStartInPresentationText, presentationText.length()));
            int startIndex = Math.min(selectionStartInPresentationText, cursorPosition);
            float startPosition = applet.textWidth(presentationText.substring(0, startIndex));
            int endIndex = Math.max(selectionStartInPresentationText, cursorPosition);
            float endPosition = applet.textWidth(presentationText.substring(0, endIndex));
            applet.fill(0, 0, 150);
            applet.noStroke();
            applet.rect(padding + startPosition, getHeight() * 0.2f, endPosition - startPosition, getHeight() * 0.6f);

        }

        if (isEnabled()) {
            applet.fill(LookAndFeel.COLOR_WHITE);
        } else {
            applet.fill(LookAndFeel.COLOR_GRAY);
        }
        applet.textAlign(LEFT, CENTER);
        applet.text(presentationText, padding, getHeight() / 2f);

        if (cursorBlinkOn && isEnabled()) {
            applet.fill(0);

            String preCursorText = presentationText.substring(0, cursorPosition);
            float curserXOffset = applet.textWidth(preCursorText);
            applet.rect(padding + curserXOffset, getHeight() * 0.2f, 1, getHeight() * 0.6f);
        }

    }

    /**
     * Updates the presented text based on the actual text and the cursor
     * position.
     */
    private void updatePresentationText() {
        float maximumPresentationTextWidth = Math.max(0, getWidth() - 2 * padding);
        getApplet().textFont(LookAndFeel.fontMedium());

        if (cursorPosition < 0) {
            if (presentationTextOffset > 0) {
                presentationTextOffset = Math.max(0, presentationTextOffset + cursorPosition);
            }
            cursorPosition = 0;
        }

        if (cursorPosition > presentationText.length()) {
            if (cursorPosition + presentationTextOffset > text.length()) {
                cursorPosition = text.length() - presentationTextOffset;
            }
            //test if the text between the presentationTextOffset and the cursor
            //would fit into the text field.
            //if not we need to move the offset over.
            String newText = text.substring(presentationTextOffset, presentationTextOffset + cursorPosition);
            float textWidth = getApplet().textWidth(newText);
            while (textWidth > maximumPresentationTextWidth) {
                presentationTextOffset += 1;
                cursorPosition -= 1;
                newText = text.substring(presentationTextOffset, presentationTextOffset + cursorPosition);
                textWidth = getApplet().textWidth(newText);
            }
        }

        String shortText = "";
        float shortTextWidth = 0;
        int shortTextLength = 0;
        while (shortTextWidth < maximumPresentationTextWidth) {
            shortTextLength++;
            if (shortTextLength + presentationTextOffset > text.length()) {
                break;
            }
            shortText = text.substring(presentationTextOffset, presentationTextOffset + shortTextLength);
            shortTextWidth = getApplet().textWidth(shortText);
        }
        if (presentationTextOffset + shortTextLength == 0) {
            shortTextLength = 1;
        }
        presentationText = text.substring(presentationTextOffset, presentationTextOffset + shortTextLength - 1);
    }

    public String getValue() {
        return text;
    }

    public void setValue(String text) {
        this.text = text;
        presentationTextOffset = 0;
        cursorPosition = 0;
        updatePresentationText();
    }

    @Override
    public void onMouseEnter() {
        if (isEnabled()) {
            getApplet().cursor(TEXT);
        }
        invalidate();
    }

    @Override
    public void onMouseLeave() {
        getApplet().cursor(ARROW);
        invalidate();
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (!isEnabled()) {
            return;
        }
        int closestPosition = 0;
        float closestDiff = getWidth();
        for (int i = 0; i < presentationText.length() + 1; i++) {
            float gapXPos = padding + getApplet().textWidth(presentationText.substring(0, i));
            float diff = Math.abs(gapXPos - x);
            if (Math.abs(gapXPos - x) < closestDiff) {
                closestDiff = Math.abs(gapXPos - x);
                closestPosition = i;
            }
        }
        cursorPosition = closestPosition;
        isMouseDown = true;
        selectionStartIndex = presentationTextOffset + cursorPosition;
        setCursorOnAndReschedule();
        invalidate();
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        isMouseDown = false;
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (isMouseDown) {
            if (x < padding) {
                cursorPosition = -1;
                updatePresentationText();
            } else if (x > getWidth() - padding) {
                cursorPosition = presentationText.length() + 1;
                updatePresentationText();
            } else {
                //recalculate the cursor position.
                int closestPosition = 0;
                float closestDiff = getWidth();
                for (int i = 0; i < presentationText.length() + 1; i++) {
                    float gapXPos = padding + getApplet().textWidth(presentationText.substring(0, i));
                    float diff = Math.abs(gapXPos - x);
                    if (Math.abs(gapXPos - x) < closestDiff) {
                        closestDiff = Math.abs(gapXPos - x);
                        closestPosition = i;
                    }
                }
                cursorPosition = closestPosition;
            }
            setCursorOnAndReschedule();
            invalidate();
        }
    }

    @Override
    public void onFocusGained() {
        setCursorOnAndReschedule();

    }

    @Override
    public void onFocusLost() {
        cursorBlinkOn = false;
        cursorBlinkTask.cancel();
        cursorBlinkTask = null;
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        //only handle key input when this is currently in focus.
        if (!isFocused()) {
            return;
        }
        if (!isEnabled()) {
            return;
        }

        if (event.getKeyCode() == VK_LEFT) {
            cursorPosition--;
            updatePresentationText();
            if (event.getModifiers() != KeyEvent.SHIFT) {
                selectionStartIndex = presentationTextOffset + cursorPosition;
            }
            setCursorOnAndReschedule();
            invalidate();
        } else if (event.getKeyCode() == VK_RIGHT) {
            cursorPosition++;
            updatePresentationText();
            if (event.getModifiers() != KeyEvent.SHIFT) {
                selectionStartIndex = presentationTextOffset + cursorPosition;
            }
            setCursorOnAndReschedule();
            invalidate();

        } else if (event.getKeyCode() == VK_END) {
            cursorPosition -= text.length();
            updatePresentationText();
            if (event.getModifiers() != KeyEvent.SHIFT) {
                selectionStartIndex = presentationTextOffset + cursorPosition;
            }
            setCursorOnAndReschedule();
            invalidate();
        } else if (event.getKeyCode() == VK_HOME) {
            cursorPosition += text.length();
            updatePresentationText();
            if (event.getModifiers() != KeyEvent.SHIFT) {
                selectionStartIndex = presentationTextOffset + cursorPosition;
            }
            setCursorOnAndReschedule();
            invalidate();
        } else if (event.getKeyCode() == VK_BACK_SPACE) {
            if (!isSelectionActive()
                    && (cursorPosition + presentationTextOffset) != 0) {
                cursorPosition -= 1;
            }
            removeSelection();
            setCursorOnAndReschedule();
            invalidate();
        } else if (event.getKeyCode() == VK_DELETE) {
            if (!isSelectionActive()
                    && cursorPosition + presentationTextOffset != text.length()) {
                cursorPosition += 1;
            }
            removeSelection();
            setCursorOnAndReschedule();
            invalidate();

        } else if (event.getKeyCode() == 67 && (event.getModifiers() & KeyEvent.CTRL) > 0) {
            //KeyCode 67 == 'c'
            //copy to clipboard
            int actualCursorPosition = presentationTextOffset + cursorPosition;
            int startIndex = Math.min(selectionStartIndex, actualCursorPosition);
            int endIndex = Math.max(selectionStartIndex, actualCursorPosition);
            String selection = text.substring(startIndex, endIndex);
            setClipboard(selection);
        } else if (event.getKeyCode() == 88 && (event.getModifiers() & KeyEvent.CTRL) > 0) {
            //KeyCode 88 = 'x'
            //Cut to clipbaord
            int actualCursorPosition = presentationTextOffset + cursorPosition;
            int startIndex = Math.min(selectionStartIndex, actualCursorPosition);
            int endIndex = Math.max(selectionStartIndex, actualCursorPosition);
            String selection = text.substring(startIndex, endIndex);
            setClipboard(selection);
            removeSelection();
            invalidate();
        } else if (event.getKeyCode() == 86 && (event.getModifiers() & KeyEvent.CTRL) > 0) {
            //KeyCode 86 == 'v'
            //past from clipboard
            String clipboard = getClipBoard().replaceAll("\n", "");
            text = text.substring(0, presentationTextOffset + cursorPosition)
                    + clipboard
                    + text.substring(presentationTextOffset + cursorPosition, text.length());
            cursorPosition += getClipBoard().length();
            updatePresentationText();
            setCursorOnAndReschedule();
            invalidate();
        } else if (event.getKeyCode() == 65 && (event.getModifiers() & KeyEvent.CTRL) > 0) {
            //KeyCode 65 == 'a'
            //select all
            selectionStartIndex = 0;
            cursorPosition = text.length();
            updatePresentationText();
            invalidate();
        } else if (isPrintableChar(event.getKey())) {
            if (isSelectionActive()) {
                removeSelection();
            }
            text = text.substring(0, presentationTextOffset + cursorPosition)
                    + event.getKey()
                    + text.substring(presentationTextOffset + cursorPosition, text.length());
            cursorPosition++;
            selectionStartIndex++;
            updatePresentationText();
            setCursorOnAndReschedule();
            invalidate();
        }
    }

    @Override
    public void onResize(float w, float h) {
        updatePresentationText();
    }

    private boolean isSelectionActive() {
        return (selectionStartIndex - presentationTextOffset != cursorPosition);
    }

    private void removeSelection() {
        int actualCursorPosition = presentationTextOffset + cursorPosition;
        int startIndex = Math.min(selectionStartIndex, actualCursorPosition);
        int endIndex = Math.max(selectionStartIndex, actualCursorPosition);
        text = text.substring(0, startIndex) + text.substring(endIndex, text.length());
        if (actualCursorPosition > selectionStartIndex) {
            cursorPosition -= endIndex - startIndex;
        }
        updatePresentationText();
        selectionStartIndex = cursorPosition + presentationTextOffset;
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

    private void setClipboard(String selection) {
        StringSelection strSel = new StringSelection(selection);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strSel, null);
    }

    private boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c))
                && c != CHAR_UNDEFINED
                && block != null
                && block != Character.UnicodeBlock.SPECIALS;
    }

    private void setCursorOnAndReschedule() {
        if (cursorBlinkTask != null) {
            cursorBlinkTask.cancel();
        }
        cursorBlinkOn = true;
        cursorBlinkTask = new CursorBlinkTask();
        cursorBlinkTimer.schedule(cursorBlinkTask, 700, 700);

    }

    public class CursorBlinkTask extends TimerTask {

        @Override
        public void run() {
            cursorBlinkOn = !cursorBlinkOn;
            invalidate();
        }
    }

}
