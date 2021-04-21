/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking;

import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.DriverInfo;
import base.screen.networking.data.LapInfo;
import base.screen.networking.data.RealtimeInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.data.TrackInfo;
import base.screen.networking.enums.BroadcastingEventType;
import base.screen.networking.enums.CarLocation;
import base.screen.networking.enums.DriverCategory;
import base.screen.networking.enums.LapType;
import base.screen.networking.enums.Nationality;
import base.screen.networking.enums.SessionPhase;
import base.screen.networking.enums.SessionType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class AccBroadcastingProtocol {

    private static Logger LOG = Logger.getLogger(AccBroadcastingClient.class.getName());

    public interface OutboundMessageTypes {

        public byte REGISTER_COMMAND_APPLICATION = 0x01;
        public byte UNREGISTER_COMMAND_APPLICATION = 0x09;
        public byte REQUEST_ENTRY_LIST = 0x0A;
        public byte REQUEST_TRACK_DATA = 0x0B;
        public byte CHANGE_HUD_PAGE = 0x31;
        public byte CHANGE_FOCUS = 0x32;
        public byte INSTANT_REPLAY_REQUEST = 0x33;
        public byte PLAY_MANUAL_REPLAY_HIGHLIGHTS = 0x34;
        public byte SAVE_MANUAL_REPLAY_HIGHLIGHTS = 0x3C;
    }

    public interface InboundMessageTypes {

        public byte REGISTRATION_RESULT = 0x01;
        public byte REALTIME_UPDATE = 0x02;
        public byte REALTIME_CAR_UPDATE = 0x03;
        public byte ENTRY_LIST = 4;
        public byte TRACK_DATA = 5;
        public byte ENTRY_LIST_CAR = 6;
        public byte BROADCASTING_EVENT = 0x07;
    }

    /**
     * Version of the broadcasting protocoll.
     */
    private static final byte BROADCASTING_PROTOCOL_VERSION = 0x04;
    /**
     * Callback to trigger the events.
     */
    private AccBroadcastingClientListener callback;

    /**
     * Because the entryList is split in multiple packets we need this as a
     * temporary holder
     */
    private Map<Integer, CarInfo> carEntries = new HashMap<>();

    private long lastTimeEntryListRequest = 0;

    public AccBroadcastingProtocol(AccBroadcastingClientListener callback) {
        this.callback = callback;
    }

    public void processMessage(ByteArrayInputStream in) {
        byte messageType = readByte(in);
        switch (messageType) {
            case InboundMessageTypes.REGISTRATION_RESULT:
                readRegistrationResult(in);
                break;
            case InboundMessageTypes.REALTIME_UPDATE:
                readRealtimeUpdate(in);
                break;
            case InboundMessageTypes.REALTIME_CAR_UPDATE:
                readRealtimeCarUpdate(in);
                break;
            case InboundMessageTypes.ENTRY_LIST:
                readEntryList(in);
                break;
            case InboundMessageTypes.TRACK_DATA:
                readTrackData(in);
                break;
            case InboundMessageTypes.ENTRY_LIST_CAR:
                readEntryListCar(in);
                break;
            case InboundMessageTypes.BROADCASTING_EVENT:
                readBroadcastingEvent(in);
                break;

            default:
                LOG.warning("Unknown message type: " + messageType);
        }
    }

    private void readRegistrationResult(ByteArrayInputStream in) {
        int connectionID = readInt32(in);
        boolean connectionSuccess = readByte(in) > 0;
        boolean isReadonly = readByte(in) == 0;
        String errorMessage = readString(in);
        callback.onRegistrationResult(connectionID, connectionSuccess, isReadonly, errorMessage);
    }

    private void readRealtimeUpdate(ByteArrayInputStream in) {
        int eventIndex = readUInt16(in);
        int sessionIndex = readUInt16(in);
        SessionType sessionType = SessionType.fromId(readByte(in));
        SessionPhase phase = SessionPhase.fromId(readByte(in));
        float sessionTime = readFloat(in);
        float sessionEndTime = readFloat(in);

        int focusedCarIndex = readInt32(in);
        String activeCameraSet = readString(in);
        String activeCamera = readString(in);
        String currentHudPage = readString(in);

        boolean isReplayPlaying = readByte(in) > 0;
        float replaySessionTime = 0;
        float replayRemainingTime = 0;
        if (isReplayPlaying) {
            replaySessionTime = readFloat(in);
            replayRemainingTime = readFloat(in);
        }

        float timeOfDay = readFloat(in);
        byte ambientTemp = readByte(in);
        byte trackTemp = readByte(in);
        byte cloudLevel = readByte(in);
        byte rainLevel = readByte(in);
        byte wetness = readByte(in);

        LapInfo bestSessionLap = readLap(in);

        SessionInfo sessionInfo = new SessionInfo(eventIndex, sessionIndex, sessionType, phase, sessionTime,
                sessionEndTime, focusedCarIndex, activeCameraSet, activeCamera, currentHudPage, isReplayPlaying,
                replaySessionTime, replayRemainingTime, timeOfDay, ambientTemp, trackTemp, cloudLevel,
                rainLevel, wetness, bestSessionLap);

        callback.onRealtimeUpdate(sessionInfo);
    }

    private void readRealtimeCarUpdate(ByteArrayInputStream in) {
        int carId = readUInt16(in);
        int driverIndex = readUInt16(in);
        byte driverCount = readByte(in);
        byte gear = readByte(in);
        float posX = readFloat(in);
        float posY = readFloat(in);
        float yaw = readFloat(in);
        CarLocation location = CarLocation.fromId(readByte(in));
        int kmh = readUInt16(in);
        int position = readUInt16(in);
        int cupPosition = readUInt16(in);
        int trackPosition = readUInt16(in);
        float splinePosition = readFloat(in);
        int laps = readUInt16(in);
        int delta = readInt32(in);
        LapInfo bestSessionLap = readLap(in);
        LapInfo lasLap = readLap(in);
        LapInfo currentLap = readLap(in);

        RealtimeInfo info = new RealtimeInfo(carId, driverIndex, driverCount, gear, posX, posY, yaw,
                location, kmh, position, cupPosition, trackPosition, splinePosition, laps, delta,
                bestSessionLap, lasLap, currentLap);
        callback.onRealtimeCarUpdate(info);
    }

    private void readEntryList(ByteArrayInputStream in) {
        List<Integer> cars = new LinkedList<>();

        int connectionId = readInt32(in);
        int carEntryCount = readUInt16(in);
        for (int i = 0; i < carEntryCount; i++) {
            cars.add(readUInt16(in));
        }
        callback.onEntryListUpdate(cars);
    }

    private void readEntryListCar(ByteArrayInputStream in) {
        int carId = readUInt16(in);
        byte carModelType = readByte(in);
        String teamName = readString(in);
        int raceNumber = readInt32(in);
        byte cupCatergory = readByte(in);
        byte currentDriverIndex = readByte(in);
        int carNationality = readUInt16(in);

        int _driverCount = readByte(in);
        List<DriverInfo> drivers = new LinkedList<>();
        for (int i = 0; i < _driverCount; i++) {
            String firstName = readString(in);
            String lastName = readString(in);
            String shortName = readString(in);
            DriverCategory category = DriverCategory.fromId(readByte(in));
            Nationality driverNationality = Nationality.fromId(readUInt16(in));
            drivers.add(new DriverInfo(firstName, lastName, shortName, category, driverNationality));
        }
        CarInfo carInfo = new CarInfo(carId, carModelType, teamName, raceNumber,
                cupCatergory, currentDriverIndex, carNationality, drivers,
                new RealtimeInfo());
        callback.onEntryListCarUpdate(carInfo);
    }

    private void readBroadcastingEvent(ByteArrayInputStream in) {
        BroadcastingEventType type = BroadcastingEventType.fromId(readByte(in));
        String msg = readString(in);
        int timeMs = readInt32(in);
        int carId = readInt32(in);

        BroadcastingEvent event = new BroadcastingEvent(type, msg, timeMs, carId);
        callback.onBroadcastingEvent(event);
    }

    private void readTrackData(ByteArrayInputStream in) {
        int connectionID = readInt32(in);
        String trackName = readString(in);
        int trackId = readInt32(in);
        int trackMeters = readInt32(in);

        Map<String, List<String>> cameraSets = new HashMap<>();
        byte cameraSetCount = readByte(in);
        for (int camSet = 0; camSet < cameraSetCount; camSet++) {

            String camSetName = readString(in);
            cameraSets.put(camSetName, new LinkedList<>());

            byte cameraCount = readByte(in);
            for (int cam = 0; cam < cameraCount; cam++) {

                String camName = readString(in);
                cameraSets.get(camSetName).add(camName);
            }
        }

        List<String> hudPages = new LinkedList<>();
        byte hudPagesCount = readByte(in);
        for (int i = 0; i < hudPagesCount; i++) {
            hudPages.add(readString(in));
        }

        TrackInfo info = new TrackInfo(trackName, trackId, trackMeters, cameraSets, hudPages);
        callback.onTrackData(info);
    }

    public static byte[] buildRegisterRequest(String name, String password, int interval, String commandPassword) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REGISTER_COMMAND_APPLICATION);
        message.write(BROADCASTING_PROTOCOL_VERSION);
        writeString(message, name);
        writeString(message, password);
        message.write(toByteArray(interval, 4), 0, 4);
        writeString(message, commandPassword);
        return message.toByteArray();
    }

    public static byte[] buildUnregisterRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.UNREGISTER_COMMAND_APPLICATION);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    public static byte[] buildFocusRequest(int connectionId, Integer carIndex, String cameraSet, String camera) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.CHANGE_FOCUS);
        message.write(toByteArray(connectionId, 4), 0, 4);
        if (carIndex == null) {
            message.write(0);   //no change of car
        } else {
            message.write(1);
            message.write(toByteArray(carIndex, 2), 0, 2);
        }
        if (cameraSet == null || camera == null || cameraSet.isEmpty() || camera.isEmpty()) {
            message.write(0);   //no change of camera
        } else {
            message.write(1);
            writeString(message, cameraSet);
            writeString(message, camera);
        }
        return message.toByteArray();
    }

    public static byte[] buildEntryListRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_ENTRY_LIST);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    public static byte[] buildTrackDataRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_TRACK_DATA);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    private static void writeString(ByteArrayOutputStream o, String message) {
        o.write(toByteArray(message.length(), 2), 0, 2);
        o.write(message.getBytes(), 0, message.length());
    }

    private static byte[] toByteArray(int n, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (n & 0xFF);
            n = n >> 8;
        }
        return result;
    }

    private byte readByte(ByteArrayInputStream in) {
        return (byte) in.read();
    }

    private int readUInt16(ByteArrayInputStream in) {
        byte[] int32 = new byte[2];
        in.read(int32, 0, 2);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private int readInt32(ByteArrayInputStream in) {
        byte[] int32 = new byte[4];
        in.read(int32, 0, 4);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private String readString(ByteArrayInputStream in) {
        int length = readUInt16(in);
        byte[] message = new byte[length];
        in.read(message, 0, length);
        return new String(message, StandardCharsets.UTF_8);
    }

    private float readFloat(ByteArrayInputStream in) {
        byte[] int32 = new byte[4];
        in.read(int32, 0, 4);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private LapInfo readLap(ByteArrayInputStream in) {

        int lapTimeMS = readInt32(in);
        int carIndex = readUInt16(in);
        int driverIndex = readUInt16(in);

        int splitCount = readByte(in);
        List<Integer> splits = new LinkedList<>();
        for (int i = 0; i < splitCount; i++) {
            splits.add(readInt32(in));
        }
        for(int i=splitCount; i<3;i++){
            splits.add(0);
        }

        boolean isInvalid = readByte(in) > 0;
        boolean isValidForBest = readByte(in) > 0;

        boolean isOutLap = readByte(in) > 0;
        boolean isInLap = readByte(in) > 0;
        LapType type = LapType.REGULAR;
        if (isOutLap) {
            type = LapType.OUTLAP;
        } else if (isInLap) {
            type = LapType.INLAP;
        }

        return new LapInfo(lapTimeMS, carIndex, driverIndex, splits, isInvalid, isValidForBest, type);
    }

}
