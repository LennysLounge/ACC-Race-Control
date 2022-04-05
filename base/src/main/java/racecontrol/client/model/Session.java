/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

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

    public synchronized Session copy() {
        Session session = new Session();
        session.raw = raw;
        return session;
    }
}
