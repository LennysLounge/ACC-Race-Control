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
        entries.add(new ListEntry(Arrays.asList("1", "K. Bond", "80", "01:49.353"), false, DriverCategory.SILVER));
        entries.add(new ListEntry(Arrays.asList("2", "P. Hold [FRT]", "81", "+2.427"), false, DriverCategory.SILVER));
        entries.add(new ListEntry(Arrays.asList("3", "D. Almeida", "18", "+3.306"), false, DriverCategory.BRONZE));
        entries.add(new ListEntry(Arrays.asList("4", "E. Rincon", "10", "--"), true, DriverCategory.BRONZE));
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
