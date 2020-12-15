/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.networking;

import acclivetiming.monitor.networking.data.BroadcastingEvent;
import acclivetiming.monitor.networking.data.CarInfo;
import acclivetiming.monitor.networking.data.RealtimeInfo;
import acclivetiming.monitor.networking.data.SessionInfo;
import acclivetiming.monitor.networking.data.TrackInfo;
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
