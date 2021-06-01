/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.utility;

/**
 *
 * @author Leonard
 */
public class TimeUtils {

    public static String asDuration(float millis) {
        float ms = millis % 1000;
        float remaining = (millis - ms) / 1000;
        float s = remaining % 60;
        remaining = (remaining - s) / 60;
        float m = remaining % 60;
        remaining = (remaining - m) / 60;
        float h = remaining % 60;
        return String.format("%02d:%02d:%02d", (int) h, (int) m, (int) s);
    }

    public static String asDurationShort(float millis) {
        float ms = millis % 1000;
        float remaining = (millis - ms) / 1000;
        float s = remaining % 60;
        remaining = (remaining - s) / 60;
        float m = remaining % 60;
        remaining = (remaining - m) / 60;
        float h = remaining % 60;
        if (h > 1) {
            return String.format("%d:%02d:%02d", (int) h, (int) m, (int) s);
        }
        return String.format("%02d:%02d", (int) m, (int) s);
    }

    public static String asLapTime(float millis) {
        float ms = millis % 1000;
        float remaining = (millis - ms) / 1000;
        float s = remaining % 60;
        remaining = (remaining - s) / 60;
        float m = remaining % 60;
        remaining = (remaining - m) / 60;
        float h = remaining % 60;
        return String.format("%02d:%02d.%03d", (int) m, (int) s, (int) ms);
    }

    public static String asDelta(float millis) {
        String sign = Math.signum(millis) < 0 ? "-" : "+";
        millis = Math.abs(millis);
        float ms = millis % 1000;
        float remaining = (millis - ms) / 1000;
        float s = remaining % 60;
        remaining = (remaining - s) / 60;
        float m = remaining % 60;
        remaining = (remaining - m) / 60;
        float h = remaining % 60;
        if (m > 1) {
            return String.format("%s%01d:%02d.%03d", sign, (int) m, (int) s, (int) ms);
        }
        return String.format("%s%01d.%03d", sign, (int) s, (int) ms);
    }

    public static String asGap(float millis) {
        String sign = Math.signum(millis) < 0 ? "-" : "+";
        millis = Math.abs(millis);
        float ms = millis % 1000;
        float remaining = (millis - ms) / 1000;
        float s = remaining % 60;
        remaining = (remaining - s) / 60;
        float m = remaining % 60;
        if (m > 0) {
            return String.format("%+d:%02d.%d", (int) m, (int) s, (int) Math.floor(ms / 100));
        }
        return String.format("%+d.%d", (int) s, (int) Math.floor(ms / 100));
    }

    public static float durationAsMillis(String duration) {
        String[] partial = duration.split(":");
        int h = Integer.valueOf(partial[0]);
        int m = Integer.valueOf(partial[1]);
        int s = Integer.valueOf(partial[2]);
        return (((h * 60) + m) * 60 + s) * 1000;
    }

}
