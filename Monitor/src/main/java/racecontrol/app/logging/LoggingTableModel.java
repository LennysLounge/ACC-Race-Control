/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.logging;

import racecontrol.LookAndFeel;
import racecontrol.lpgui.gui.LPTableColumn;
import racecontrol.lpgui.gui.LPTable;
import racecontrol.lpgui.gui.TableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import racecontrol.logging.LogMessage;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LoggingTableModel extends TableModel {

    private final LPTable.CellRenderer messageRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        String message = (String) context.object;
        applet.fill(255);
        applet.textAlign(LEFT, CENTER);
        float x = context.height / 2;
        int tabSize = 140;
        String[] partials = message.split("\t");
        applet.textFont(LookAndFeel.fontRegular());
        for (String partial : partials) {
            applet.text(partial, x, context.height / 2f);
            float msgWidth = applet.textWidth(partial);
            x += (msgWidth - (msgWidth % tabSize) + tabSize);
        }
    };

    private List<LogMessage> messages = new LinkedList<>();

    @Override
    public int getRowCount() {
        return messages.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("Time")
            .setMaxWidth(100)
            .setMinWidth(100),
            new LPTableColumn("Message")
            .setCellRenderer(messageRenderer)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        LogMessage message = messages.get(messages.size() - row - 1);
        switch (column) {
            case 0:
                DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                return dateFormat.format(message.getTimeStamp());
            case 1:
                return message.getMessage();
        }
        return "-";
    }

    public void setMessages(List<LogMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(LogMessage message) {
        messages.add(message);
    }

}
