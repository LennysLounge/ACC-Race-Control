/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.logging;

import processing.core.PApplet;
import racecontrol.gui.CustomPApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.logging.LogMessage;
import racecontrol.logging.LoggerListener;
import racecontrol.logging.UILogger;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.table.LPTable;

/**
 *
 * @author Leonard
 */
public class LoggingPanel
        extends LPContainer
        implements LoggerListener, PageController {

    /**
     * The table that display the messages.
     */
    private final LPTable table = new LPTable();

    /**
     * Table model for the logging extension.
     */
    private final LoggingTableModel model = new LoggingTableModel();

    private final MenuItem menuItem;

    public LoggingPanel() {
        setName("Logging");

        table.setTableModel(model);
        addComponent(table);
        UILogger.register(this);

        this.menuItem = new MenuItem("Log",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_LOG.png"));
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        table.setPosition(10, 0);
        table.setSize(w - 20, h - 10);
    }

    @Override
    public void messageLogged(String message) {
        model.addMessage(new LogMessage(message));
        invalidate();
    }

    @Override
    public LPContainer getPanel() {
        return this;
    }

    @Override
    public Menu.MenuItem getMenuItem() {
        return menuItem;
    }
}
