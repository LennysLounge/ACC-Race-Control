/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import racecontrol.client.protocol.SessionInfo;

/**
 * Represents a session.
 *
 * @author Leonard
 */
public class Session {

    /**
     * Raw session info object.
     */
    public SessionInfo raw = new SessionInfo();
    /**
     * Best sector times.
     */
    public List<Integer> sessionBestSplits = Arrays.asList(Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE);
    /**
     * Maximum speed reached in this session.
     */
    public int maxKMH;
    /**
     * Maximum speed reached at the speed trap in this session.
     */
    public int maxSpeedTrapKMH;

    public synchronized Session copy() {
        Session session = new Session();
        session.raw = raw;
        session.sessionBestSplits = new ArrayList<>(sessionBestSplits);
        session.maxKMH = maxKMH;
        session.maxSpeedTrapKMH = maxSpeedTrapKMH;
        return session;
    }
}
