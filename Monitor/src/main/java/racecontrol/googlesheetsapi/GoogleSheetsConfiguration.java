/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.googlesheetsapi;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsConfiguration {

    public final static String FIND_EMPTY_ROW_RANGE = "B1:D";
    public final static String REPLAY_OFFSET_CELL = "C2";
    public final static String SESSION_TIME_COLUMN = "B";
    public final static String CAR_INFO_COLUMN = "D";

    private final String spreadsheetLink;
    private final String credentialsPath;
    private final String findEmptyRowRange;
    private final String replayOffsetCell;
    private final String sessionTimeColumn;
    private final String carInfoColumn;

    public GoogleSheetsConfiguration(String spreadsheetLink,
            String credentialsPath,
            String findEmptyRowRange,
            String replayOffsetCell,
            String sessionTimeColumn,
            String carInfoColumn) {
        this.spreadsheetLink = spreadsheetLink;
        this.credentialsPath = credentialsPath;
        this.findEmptyRowRange = findEmptyRowRange;
        this.replayOffsetCell = replayOffsetCell;
        this.sessionTimeColumn = sessionTimeColumn;
        this.carInfoColumn = carInfoColumn;
    }

    public String getSpreadsheetLink() {
        return spreadsheetLink;
    }

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public String getFindEmptyRowRange() {
        return findEmptyRowRange;
    }

    public String getReplayOffsetCell() {
        return replayOffsetCell;
    }

    public String getSessionTimeColumn() {
        return sessionTimeColumn;
    }

    public String getCarInfoColumn() {
        return carInfoColumn;
    }

}
