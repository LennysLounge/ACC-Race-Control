/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.utility;

/**
 *
 * @author Leonard
 */
public class TimeUtils {

    /**
     * Renders the time as a duration in the hh:mm:ss format.
     *
     * @param millis Time in milliseconds.
     * @return String with the time repesentation.
     */
    public static String asDuration(int millis) {
        int ms = millis % 1000;
        int remaining = (millis - ms) / 1000;
        int s = remaining % 60;
        remaining = (remaining - s) / 60;
        int m = remaining % 60;
        remaining = (remaining - m) / 60;
        int h = remaining % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * Renders the time as a duration in the (hh:)mm:ss format. Hours are
     * omitted if the duration is less than one hour.
     *
     * @param millis Time in milliseconds.
     * @return String with the time repesentation.
     */
    public static String asDurationShort(int millis) {
        int ms = millis % 1000;
        int remaining = (millis - ms) / 1000;
        int s = remaining % 60;
        remaining = (remaining - s) / 60;
        int m = remaining % 60;
        remaining = (remaining - m) / 60;
        int h = remaining % 60;
        if (h >= 1) {
            return String.format("%d:%02d:%02d", (int) h, (int) m, (int) s);
        }
        return String.format("%02d:%02d", m, s);
    }

    /**
     * Renders the time as a lap time in the mm:ss.SSS format.
     *
     * @param millis Time in milliseconds.
     * @return String with the time repesentation.
     */
    public static String asLapTime(int millis) {
        int ms = millis % 1000;
        int remaining = (millis - ms) / 1000;
        int s = remaining % 60;
        remaining = (remaining - s) / 60;
        int m = remaining % 60;
        return String.format("%d:%02d.%03d", m, s, ms);
    }

    /**
     * Renders the time as a delta time in the format (- | +)(mm:)ss.SSS with
     * leading sign and minutes are ommited if possible.
     *
     * @param millis Time in milliseconds.
     * @return String with the time repesentation.
     */
    public static String asDelta(int millis) {
        String sign = Math.signum(millis) < 0 ? "-" : "+";
        millis = Math.abs(millis);
        int ms = millis % 1000;
        int remaining = (millis - ms) / 1000;
        int s = remaining % 60;
        remaining = (remaining - s) / 60;
        int m = remaining % 60;
        remaining = (remaining - m) / 60;
        int h = remaining % 60;
        if (m >= 1) {
            return String.format("%s%01d:%02d.%03d", sign, m, s, ms);
        }
        return String.format("%s%01d.%03d", sign, s, ms);
    }

    /**
     * Renders the time as a delta time in the format (- | +)(mm:)ss.S with
     * leading sign and minutes are ommited if possible.
     *
     * @param millis Time in milliseconds.
     * @return String with the time repesentation.
     */
    public static String asGap(int millis) {
        String sign = Math.signum(millis) < 0 ? "-" : "+";
        millis = Math.abs(millis);
        int ms = millis % 1000;
        int remaining = (millis - ms) / 1000;
        int s = remaining % 60;
        remaining = (remaining - s) / 60;
        int m = remaining % 60;
        if (m > 0) {
            return String.format("%s%d:%02d.%d", sign, m, s, (int) Math.floor(ms / 100));
        }
        return String.format("%s%d.%d", sign, s, (int) Math.floor(ms / 100));
    }

    /**
     * Converts a string time into a duration in milliseconds.
     *
     * @param duration The duration text.
     * @return The time in milliseconds.
     */
    public static int durationAsMillis(String duration) {
        String[] partial = duration.split(":");
        int h = Integer.valueOf(partial[0]);
        int m = Integer.valueOf(partial[1]);
        int s = Integer.valueOf(partial[2]);
        return (((h * 60) + m) * 60 + s) * 1000;
    }

    /**
     * Renders the time in seconds in the format ss.SSS.
     *
     * @param millis The time in milliseconds.
     * @return String with the time repesentation.
     */
    public static String asSeconds(int millis) {
        int ms = millis % 1000;
        int remaining = (millis - ms) / 1000;
        int s = remaining;
        return String.format("%2d.%03d", s, ms);
    }

}
