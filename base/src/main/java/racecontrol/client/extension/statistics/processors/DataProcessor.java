/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.protocol.CarInfo;
import racecontrol.client.protocol.DriverInfo;
import racecontrol.client.protocol.LapInfo;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.eventbus.Event;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_ID;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_MODEL;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarStatistics.CATEGORY;
import static racecontrol.client.extension.statistics.CarStatistics.CUP_POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarStatistics.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.DELTA;
import static racecontrol.client.extension.statistics.CarStatistics.DRIVER_INDEX;
import static racecontrol.client.extension.statistics.CarStatistics.DRIVER_LIST;
import racecontrol.client.extension.statistics.CarStatistics.DriverList;
import static racecontrol.client.extension.statistics.CarStatistics.FIRSTNAME;
import static racecontrol.client.extension.statistics.CarStatistics.FULL_NAME;
import static racecontrol.client.extension.statistics.CarStatistics.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarStatistics.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarStatistics.IS_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarStatistics.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarStatistics.LAST_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarStatistics.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.LAST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarStatistics.LAST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarStatistics.LAST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarStatistics.NAME;
import static racecontrol.client.extension.statistics.CarStatistics.POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.PREDICTED_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_ID;
import static racecontrol.client.extension.statistics.CarStatistics.SHORT_NAME;
import static racecontrol.client.extension.statistics.CarStatistics.SURNAME;
import static racecontrol.client.extension.statistics.CarStatistics.TEAM_NAME;

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

    public DataProcessor(Map<Integer, CarStatisticsWritable> cars) {
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
        CarInfo carInfo = client.getModel().cars.get(info.getCarId()).raw;
        if (!getCars().containsKey(info.getCarId())) {
            return;
        }
        CarStatisticsWritable car = getCars().get(info.getCarId());

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
        car.put(DRIVER_INDEX, (int) info.getDriverIndex());
        car.put(DRIVER_LIST, new DriverList(carInfo.getDrivers()));
        car.put(TEAM_NAME, carInfo.getTeamName());
        // Laps
        car.put(CURRENT_LAP_TIME, info.getCurrentLap().getLapTimeMS());
        car.put(LAST_LAP_TIME, info.getLastLap().getLapTimeMS());
        car.put(BEST_LAP_TIME, info.getBestSessionLap().getLapTimeMS());
        car.put(DELTA, info.getDelta());
        car.put(PREDICTED_LAP_TIME, info.getBestSessionLap().getLapTimeMS() + info.getDelta());
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
        for (CarStatisticsWritable car : getCars().values()) {
            car.put(IS_FOCUSED_ON, info.getFocusedCarIndex() == car.get(CAR_ID));
            car.put(SESSION_ID, client.getModel().currentSessionId);
            car.put(IS_SESSION_BEST,
                    info.getBestSessionLap().getLapTimeMS() != Integer.MAX_VALUE
                    && info.getBestSessionLap().getLapTimeMS() == car.get(BEST_LAP_TIME)
            );
        }
    }

    private Integer intOrDefault(Integer i, int d) {
        if (i == null) {
            return d;
        }
        return i;
    }
}
