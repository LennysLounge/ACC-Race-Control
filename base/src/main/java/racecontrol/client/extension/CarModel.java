/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension;

/**
 *
 * @author Leonard
 */
public class CarModel {

    protected int carId;
    protected int carNumber;

    public int getCarId() {
        return carId;
    }

    public int getCarNumber() {
        return carNumber;
    }

    public static class CarModelWritable
            extends CarModel {

        public void setCarId(int carId) {
            this.carId = carId;
        }

        public void setCarNumber(int carNumber) {
            this.carNumber = carNumber;
        }

    }

}
