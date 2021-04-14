/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.screen.Main;
import base.screen.networking.events.RealtimeCarUpdate;
import base.screen.networking.RealtimeUpdate;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.networking.AccBroadcastingClient;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.RealtimeInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.visualisation.gui.LPContainer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtension
        implements EventListener, AccClientExtension {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(LiveTimingExtension.class.getName());
    /**
     * Reference to the client.
     */
    private final AccBroadcastingClient client;
    /**
     * The visualisation panel
     */
    private final LiveTimingPanel panel;
    /**
     * Map from carId to ListEntry.
     */
    private final Map<Integer, CarInfo> entires = new HashMap<>();
    /**
     * Table model to display the live timing.
     */
    private LiveTimingTableModel model = new LiveTimingTableModel();

    public LiveTimingExtension() {
        this.client = Main.getClient();
        this.panel = new LiveTimingPanel(this);
        
        EventBus.register(this);
    }
    
    @Override
    public LPContainer getPanel(){
        return panel;
    }

    public LiveTimingTableModel getTableModel() {
        return model;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdate) {
            onRealtimeUpdate(((RealtimeUpdate) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdate) {
            onRealtimeCarUpdate(((RealtimeCarUpdate) e).getInfo());
        }
    }

    public void onRealtimeUpdate(SessionInfo sessionInfo) {
        List<CarInfo> sorted = entires.values().stream()
                .filter(entry -> entry.isConnected())
                .sorted((e1, e2) -> compareTo(e1, e2))
                .collect(Collectors.toList());

        model.setEntries(sorted);
        model.setFocusedCarId(sessionInfo.getFocusedCarIndex());
        panel.invalidate();
    }

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

    @Override
    public void removeExtension() {
        EventBus.unregister(this);
    }

}
