/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsConnectedEvent
        extends Event {

    private final Spreadsheet spreadsheet;

    public GoogleSheetsConnectedEvent(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }
    
    public Spreadsheet getSpreadsheet(){
        return spreadsheet;
    }

}
