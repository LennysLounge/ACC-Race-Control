/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.laptimes;

import ACCLiveTiming.client.AccClientExtension;
import ACCLiveTiming.extensions.incidents.IncidentExtension;
import ACCLiveTiming.networking.data.BroadcastingEvent;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.data.LapInfo;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.enums.LapType;
import ACCLiveTiming.utility.TimeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class LapTimeExtension extends AccClientExtension {

    private static Logger LOG = Logger.getLogger(IncidentExtension.class.getName());

    private final Map<Integer, Integer> lapCount = new HashMap<>();

    public LapTimeExtension() {
    }

    @Override
    public void afterPacketReceived(byte type) {

    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
        if (lapCount.containsKey(info.getCarId())) {
            if (lapCount.get(info.getCarId()) != info.getLaps()) {
                LapInfo lap = info.getLastLap();
                boolean isPersonalBest = lap.getLapTimeMS() == info.getBestSessionLap().getLapTimeMS();
                boolean isSessionBest = lap.getLapTimeMS() == client.getModel().getSessionInfo().getBestSessionLap().getLapTimeMS();
                onLapComplete(lap, isPersonalBest, isSessionBest);
            }
        }
        lapCount.put(info.getCarId(), info.getLaps());
    }

    private void onLapComplete(LapInfo lap, boolean isPB, boolean isSB) {
        CarInfo car = client.getModel().getCar(lap.getCarId());

        String message = "Lap completed: #" + car.getCarNumber()
                + "\t" + TimeUtils.asLapTime(lap.getLapTimeMS()) + "\t";
        if (isPB) {
            message += "[Personal Best]";
        }
        if (isSB) {
            message += "[Session Best]";
        }
        if (lap.getType() == LapType.INLAP) {
            message += "[Inlap]";
        }
        if (lap.getType() == LapType.OUTLAP) {
            message += "[Outlap]";
        }
        client.log(message);
        LOG.info(message);
    }

    @Override
    public void onLapComplete(BroadcastingEvent event) {

    }

    @Override
    public void onBestSessionLap(BroadcastingEvent event) {

    }

    @Override
    public void onBestPersonalLap(BroadcastingEvent event) {

    }

}
