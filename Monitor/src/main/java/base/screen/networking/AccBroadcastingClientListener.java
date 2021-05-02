/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking;

import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.RealtimeInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.data.TrackInfo;
import java.util.List;

/**
 *
 * @author Leonard
 */
public interface AccBroadcastingClientListener {

    public void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message);

    public void onRealtimeUpdate(SessionInfo sessionInfo);

    public void onRealtimeCarUpdate(RealtimeInfo info);

    public void onEntryListUpdate(List<Integer> carIds);

    public void onTrackData(TrackInfo info);

    public void onEntryListCarUpdate(CarInfo carInfo);

    public void onBroadcastingEvent(BroadcastingEvent event);

    public void afterPacketReceived(byte type);
}
