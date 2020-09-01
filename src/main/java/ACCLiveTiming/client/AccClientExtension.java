/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.client;

import ACCLiveTiming.networking.data.BroadcastingEvent;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.data.SessionInfo;
import ACCLiveTiming.networking.data.TrackInfo;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class AccClientExtension {
    /**
     * Base client for this extension.
     */
    protected BasicAccBroadcastingClient client;
    /**
     * The panel that is attached to this extension.
     * If null then no panel is attached.
     */
    protected ExtensionPanel panel = null;
    /**
     * Returns true if there is an extension Panel for this extension.
     * @return If there is an extension.
     */
    public boolean hasPanel(){
        return (panel != null);
    }
    /**
     * Returns the attached panel for this extension.
     * @return The attached panel.
     */
    public ExtensionPanel getPanel(){
        return panel;
    }
    
    protected void setClient(BasicAccBroadcastingClient client){
        this.client = client;
    }

    public void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message) {
    }

    public void onRealtimeUpdate(SessionInfo sessionInfo) {
    }

    public void onRealtimeCarUpdate(RealtimeInfo info) {
    }

    public void onEntryListUpdate(List<Integer> carIds) {
    }

    public void onTrackData(TrackInfo info) {
    }

    public void onEntryListCarUpdate(CarInfo carInfo) {
    }

    public void onBroadcastingEvent(BroadcastingEvent event) {
    }

    public void afterPacketReceived(byte type) {
    }

    public void onPracticeStart() {
    }

    public void onPracticeEnd() {
    }

    public void onQualifyingStart() {
    }

    public void onQualifyingEnd() {
    }

    public void onRaceStart() {
    }

    public void onRaceEnd() {
    }
    
    public void onAccident(BroadcastingEvent event){
    }
    
    public void onLapComplete(BroadcastingEvent event){
    }
    
    public void onBestSessionLap(BroadcastingEvent event){
    }
    
    public void onBestPersonalLap(BroadcastingEvent event){
    }
      
}
