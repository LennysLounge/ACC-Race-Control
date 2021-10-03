/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.DriverInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.CarLocation.PITLANE;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CAR_MODEL;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.CATEGORY;
import static racecontrol.client.extension.statistics.CarProperties.CUP_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.DELTA;
import static racecontrol.client.extension.statistics.CarProperties.FIRSTNAME;
import static racecontrol.client.extension.statistics.CarProperties.FULL_NAME;
import static racecontrol.client.extension.statistics.CarProperties.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.NAME;
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SHORT_NAME;
import static racecontrol.client.extension.statistics.CarProperties.SURNAME;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.IS_FOCUSED_ON;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.eventbus.Event;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_INDEX;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_LIST;
import racecontrol.client.extension.statistics.CarProperties.DriverList;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.LAST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.LAST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.LAST_SECTOR_TWO;

/**
 * A Basic processor for any easily available data.
 *
 * @author Leonard
 */
public class DataProcessor extends StatisticsProcessor {

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;

    public DataProcessor(Map<Integer, WritableCarStatistics> cars) {
        super(cars);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            onRealtimeUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    public void onRealtimeCarUpdate(RealtimeInfo info) {
        CarInfo carInfo = client.getModel().getCar(info.getCarId());
        if (!getCars().containsKey(info.getCarId())) {
            return;
        }
        WritableCarStatistics car = getCars().get(info.getCarId());

        car.put(CAR_ID, info.getCarId());

        // Identity
        car.put(FIRSTNAME, carInfo.getDriver().getFirstName());
        car.put(SURNAME, carInfo.getDriver().getLastName());
        car.put(FULL_NAME, carInfo.getDriver().getFirstName() + " " + carInfo.getDriver().getLastName());
        car.put(NAME, getName(carInfo.getDriver()));
        car.put(SHORT_NAME, carInfo.getDriver().getShortName());
        car.put(CAR_NUMBER, carInfo.getCarNumber());
        car.put(CAR_MODEL, carInfo.getCarModel());
        car.put(CATEGORY, carInfo.getDriver().getCategory());
        car.put(DRIVER_INDEX, (int) carInfo.getRealtime().getDriverIndex());
        car.put(DRIVER_LIST, new DriverList(carInfo.getDrivers()));
        // Laps
        car.put(CURRENT_LAP_TIME, info.getCurrentLap().getLapTimeMS());
        car.put(LAST_LAP_TIME, info.getLastLap().getLapTimeMS());
        car.put(BEST_LAP_TIME, info.getBestSessionLap().getLapTimeMS());
        car.put(DELTA, info.getDelta());
        car.put(CURRENT_LAP_INVALID, info.getCurrentLap().isInvalid());
        car.put(LAST_LAP_INVALID, info.getLastLap().isInvalid());
        car.put(BEST_LAP_INVALID, info.getBestSessionLap().isInvalid());
        car.put(LAP_COUNT, info.getLaps());
        // Sectors
        LapInfo lap = info.getBestSessionLap();
        car.put(BEST_SECTOR_ONE, intOrDefault(lap.getSplits().get(0), 0));
        car.put(BEST_SECTOR_TWO, intOrDefault(lap.getSplits().get(1), 0));
        car.put(BEST_SECTOR_THREE, intOrDefault(lap.getSplits().get(2), 0));
        lap = info.getLastLap();
        car.put(LAST_SECTOR_ONE, intOrDefault(lap.getSplits().get(0), 0));
        car.put(LAST_SECTOR_TWO, intOrDefault(lap.getSplits().get(1), 0));
        car.put(LAST_SECTOR_THREE, intOrDefault(lap.getSplits().get(2), 0));
        // Status
        car.put(POSITION, info.getPosition());
        car.put(CUP_POSITION, info.getCupPosition());
        car.put(IS_IN_PITS, info.getLocation() == PITLANE);
        car.put(CAR_LOCATION, info.getLocation());

    }

    private String getName(DriverInfo driver) {
        String firstname = driver.getFirstName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        return String.format("%s. %s", firstname, driver.getLastName());
    }

    public void onRealtimeUpdate(SessionInfo info) {
        for (WritableCarStatistics car : getCars().values()) {
            car.put(IS_FOCUSED_ON, info.getFocusedCarIndex() == car.get(CAR_ID));
        }
    }

    private Integer intOrDefault(Integer i, int d) {
        if (i == null) {
            return d;
        }
        return i;
    }
}
