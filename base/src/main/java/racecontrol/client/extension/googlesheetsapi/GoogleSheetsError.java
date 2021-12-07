/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsError {

    /**
     * Raw exception.
     */
    private final Exception exception;
    /**
     * HTTP error code.
     */
    private final int errorCode;
    /**
     * Text reason for this error.
     */
    private final String reason;

    public GoogleSheetsError(Exception exception, int errorCode, String reason) {
        this.exception = exception;
        this.errorCode = errorCode;
        this.reason = reason;
    }

    public Exception getException() {
        return exception;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }
}
