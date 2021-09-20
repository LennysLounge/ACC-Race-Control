/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics;

import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;

/**
 * Describes methods a statistics processor must implement.
 * @author Leonard
 */
public interface StatisticsProcessor {

    public void onRealtimeCarUpdate(RealtimeInfo info);

    public void onRealtimeUpdate(SessionInfo info);
}
