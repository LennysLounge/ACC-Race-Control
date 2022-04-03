/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol.enums;

/**
 * The different car categories in the game.
 *
 * @author Leonard
 */
public enum CarCategory {
    GT3("GT3"),
    GT4("GT4"),
    ST("ST"),
    ST22("ST"),
    CUP("CUP"),
    CUP21("CUP"),
    CHL("CHL"),
    TCX("TCX"),
    NONE("None");

    private final String text;

    private CarCategory(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
