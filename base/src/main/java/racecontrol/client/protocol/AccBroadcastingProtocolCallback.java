/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol;

import racecontrol.client.protocol.BroadcastingEvent;
import racecontrol.client.protocol.CarInfo;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.protocol.TrackInfo;
import java.util.List;

/**
 *
 * @author Leonard
 */
public interface AccBroadcastingProtocolCallback {

    public void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message);

    public void onRealtimeUpdate(SessionInfo sessionInfo);

    public void onRealtimeCarUpdate(RealtimeInfo info);

    public void onEntryListUpdate(List<Integer> carIds);

    public void onTrackData(TrackInfo info);

    public void onEntryListCarUpdate(CarInfo carInfo);

    public void onBroadcastingEvent(BroadcastingEvent event);

    public void afterPacketReceived(byte type);
}
