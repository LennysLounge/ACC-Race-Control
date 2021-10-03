/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data.enums;

/**
 * The different car categories in the game.
 *
 * @author Leonard
 */
public enum CarCategory {
    GT3("GT3"),
    GT4("GT4"),
    ST("Super Torfeo"),
    CUP("Porsche Cup"),
    NONE("None");

    private final String text;

    private CarCategory(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
