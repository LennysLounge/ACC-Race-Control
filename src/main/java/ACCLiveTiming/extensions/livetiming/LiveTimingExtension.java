/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.extensions.AccClientExtension;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.data.SessionInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtension extends AccClientExtension {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(LiveTimingExtension.class.getName());
    /**
     * Map from carId to ListEntry.
     */
    private final Map<Integer, CarInfo> entires = new HashMap<>();
    /**
     * Table model to display the live timing.
     */
    private LiveTimingTableModel model = new LiveTimingTableModel();

    public LiveTimingExtension() {
        this.panel = new LiveTimingPanel(this);
    }
    
    public LiveTimingTableModel getTableModel(){
        return model;
    }

    @Override
    public void onRealtimeUpdate(SessionInfo sessionInfo) {
        List<CarInfo> sorted = entires.values().stream()
                .filter(entry -> entry.isConnected())
                .sorted((e1, e2) -> compareTo(e1, e2))
                .collect(Collectors.toList());
        
        model.setEntries(sorted);
        model.setFocusedCarId(sessionInfo.getFocusedCarIndex());
    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
        CarInfo car = client.getModel().getCarsInfo().getOrDefault(info.getCarId(), new CarInfo());
        entires.put(car.getCarId(), car);
    }

    private int compareTo(CarInfo c1, CarInfo c2) {
        return (int) Math.signum(c1.getRealtime().getPosition() - c2.getRealtime().getPosition());
    }

    private boolean isFocused(CarInfo car) {
        return car.getCarId() == client.getModel().getSessionInfo().getFocusedCarIndex();
    }

}
