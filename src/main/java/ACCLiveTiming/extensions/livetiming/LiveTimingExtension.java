/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.client.AccClientExtension;
import ACCLiveTiming.networking.data.AccBroadcastingData;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.enums.DriverCategory;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

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
     * Map of entries to their carId.
     */
    private List<ListEntry> entries = new LinkedList<>();

    public LiveTimingExtension() {
        this.panel = new LiveTimingPanel(this);
        entries.add(new ListEntry("1", "K. Bond", "80", "12", "--.--", "--.--", "-0.52", "1:00.12", "12.02", "16.45", "18.64", "1:37.59", "1:38.21",
                false, DriverCategory.SILVER));
        entries.add(new ListEntry("2", "P. Hold [FRT]", "81", "12", "+2.52", "+2.52", "+2.34", "55.12", "12.08", "16.56", "18.69", "1.38.59", "1:38.59",
                true, DriverCategory.SILVER));
        entries.add(new ListEntry("3", "D. Almeida", "18", "12", "+4.82", "+7.34", "-0.02", "0:40.89", "12.20", "16.57", "18.81", "1:38.13", "1:38.59",
                false, DriverCategory.BRONZE));
        entries.add(new ListEntry("4", "E. Rincon", "10", "11", "+6.23", "+13.56", "+0.59", "0:20.55", "12.17", "16.64", "18.71", "1:38.37", "1:38.65",
                true, DriverCategory.GOLD));
    }

    public List<ListEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
    }

}
