/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsErrorEvent
        extends Event {

    private final ErrorType errorType;

    private final Exception exception;

    public GoogleSheetsErrorEvent(ErrorType errorType,
            Exception exception) {
        this.errorType = errorType;
        this.exception = exception;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Exception getException() {
        return exception;
    }

    public static enum ErrorType {
        WRONG_FILE_TYPE;
    }

}
