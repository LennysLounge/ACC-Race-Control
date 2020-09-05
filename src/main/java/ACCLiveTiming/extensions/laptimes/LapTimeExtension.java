/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.laptimes;

import ACCLiveTiming.extensions.AccClientExtension;
import ACCLiveTiming.extensions.incidents.IncidentExtension;
import ACCLiveTiming.extensions.logging.LoggingExtension;
import ACCLiveTiming.networking.data.BroadcastingEvent;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class LapTimeExtension extends AccClientExtension {

    private static Logger LOG = Logger.getLogger(IncidentExtension.class.getName());

    public LapTimeExtension() {
    }

    @Override
    public void afterPacketReceived(byte type) {

    }

    @Override
    public void onLapComplete(BroadcastingEvent event) {
        int carNumber = client.getModel().getCar(event.getCarId()).getCarNumber();
        String msg = "Lap completed: #" + carNumber + "\t" + event.getMessage();
        LOG.info(msg);
        client.log(msg);
    }

    @Override
    public void onBestSessionLap(BroadcastingEvent event) {
        int carNumber = client.getModel().getCar(event.getCarId()).getCarNumber();
        String msg = "Lap completed: #" + carNumber + "\t" + event.getMessage() 
                + "\t[Session best]";
        LOG.info(msg);
        client.log(msg);
    }

    @Override
    public void onBestPersonalLap(BroadcastingEvent event) {
        int carNumber = client.getModel().getCar(event.getCarId()).getCarNumber();
        String msg = "Lap completed: #" + carNumber + "\t" + event.getMessage() 
                + "\t[Personal best]";
        LOG.info(msg);
        client.log(msg);
    }

}
