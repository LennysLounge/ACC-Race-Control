/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.networking;

import ACCLiveTiming.monitor.networking.data.BroadcastingEvent;
import ACCLiveTiming.monitor.networking.data.CarInfo;
import ACCLiveTiming.monitor.networking.data.RealtimeInfo;
import ACCLiveTiming.monitor.networking.data.SessionInfo;
import ACCLiveTiming.monitor.networking.data.TrackInfo;
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
