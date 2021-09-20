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
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
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
import racecontrol.client.extension.statistics.WriteableCarStatistics;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import static racecontrol.client.extension.statistics.CarProperties.IS_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;

/**
 * A Basic processor for any easily available data.
 *
 * @author Leonard
 */
public class DataProcessor
        implements StatisticsProcessor {

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;
    /**
     * Map of car Ids to car statistics.
     */
    private final Map<Integer, WriteableCarStatistics> cars;

    public DataProcessor(Map<Integer, WriteableCarStatistics> cars) {
        this.cars = cars;
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
        CarInfo carInfo = client.getModel().getCar(info.getCarId());
        if (!cars.containsKey(info.getCarId())) {
            return;
        }
        WriteableCarStatistics car = cars.get(info.getCarId());

        car.put(CAR_ID, info.getCarId());

        car.put(POSITION, info.getPosition());
        car.put(CUP_POSITION, info.getCupPosition());
        // identity
        car.put(FIRSTNAME, carInfo.getDriver().getFirstName());
        car.put(SURNAME, carInfo.getDriver().getLastName());
        car.put(FULL_NAME, carInfo.getDriver().getFirstName() + " " + carInfo.getDriver().getLastName());
        car.put(NAME, getName(carInfo.getDriver()));
        car.put(SHORT_NAME, carInfo.getDriver().getShortName());
        car.put(CAR_NUMBER, carInfo.getCarNumber());
        car.put(CAR_MODEL, carInfo.getCarModelType());
        car.put(CATEGORY, carInfo.getDriver().getCategory());
        //laps
        car.put(CURRENT_LAP_TIME, info.getCurrentLap().getLapTimeMS());
        car.put(LAST_LAP_TIME, info.getLastLap().getLapTimeMS());
        car.put(BEST_LAP_TIME, info.getBestSessionLap().getLapTimeMS());
        car.put(DELTA, info.getDelta());
        car.put(IS_LAP_INVALID, info.getCurrentLap().isInvalid());
        car.put(LAP_COUNT, info.getLaps());
        
        car.put(IS_IN_PITS, info.getLocation() != TRACK);

    }

    private String getName(DriverInfo driver) {
        String firstname = driver.getFirstName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        return String.format("%s. %s", firstname, driver.getLastName());
    }

    @Override
    public void onRealtimeUpdate(SessionInfo info) {

    }

}
