/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsDisconnetedEvent
        extends Event {

    private final GoogleSheetsError errorInfo;

    public GoogleSheetsDisconnetedEvent() {
        this(null);
    }

    public GoogleSheetsDisconnetedEvent(GoogleSheetsError errorInfo) {
        this.errorInfo = errorInfo;
    }

    public GoogleSheetsError getErrorInfo() {
        return errorInfo;
    }

    public boolean hasError() {
        return errorInfo != null;
    }

}
