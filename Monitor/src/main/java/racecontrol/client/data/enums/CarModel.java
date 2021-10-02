/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data.enums;

import static racecontrol.client.data.enums.CarCategory.CUP;
import static racecontrol.client.data.enums.CarCategory.GT3;
import static racecontrol.client.data.enums.CarCategory.GT4;
import static racecontrol.client.data.enums.CarCategory.NONE;
import static racecontrol.client.data.enums.CarCategory.ST;

/**
 * The different car models in the game.
 *
 * @author Leonard
 */
public enum CarModel {
    // GT3's
    PORSCHE_991_GT3_R(0, "Porsche 991 GT3 R", GT3),
    MERCEDES_AMG_GT3_2015(1, "Mercedes-AMG GT3", GT3),
    FERRARI_488_GT3(2, "Ferrari 488 GT3", GT3),
    AUDI_R8_LMS(3, "Audi R8 LMS", GT3),
    LAMBORGHINI_HURACAN_GT3(4, "Lamborghini Huracan GT3", GT3),
    MCLAREN_650S_GT3(5, "McLaren 650S GT3", GT3),
    NISSAN_GT_R_NISMO_GT3_2018(6, "Nissan GT-R Nismo GT3", GT3),
    BMW_M6_GT3(7, "BMW M6 GT3", GT3),
    BENTLEY_CONTINENTAL_GT3_2018(8, "Bentley Continental GT3", GT3),
    PORSCHE_991_II_GT3_CUP(9, "Porsche 991 II GT3 Cup", CUP),
    NISSAN_GT_R_NISMO_GT3_2015(10, "Nissan GT-R Nismo GT3", GT3),
    BENTLEY_CONTINENTAL_GT3_2015(11, "Bentley Continental GT3", GT3),
    AMR_V12_VANTAGE_GT3(12, "AMR V12 Vantage GT3", GT3),
    REITER_ENGINEERING_R_EX_GT3(13, "Reiter Engineering R-EX GT3", GT3),
    EMIL_FREY_JAGUAR_G3(14, "Emil Frey Jaguar G3", GT3),
    LEXUS_RC_F_GT3(15, "Lexus RC F GT3", GT3),
    LAMBORGHINI_HURACAN_GT3_EVO(16, "Lamborghini Huracan GT3 Evo", GT3),
    HONDA_NSX_GT3(17, "Honda NSX GT3", GT3),
    LAMBORGHINI_HURACAN_SUPERTROFEO(18, "Lamborghini Huracan SuperTrofeo", ST),
    AUDI_R8_LMS_EVO(19, "Audi R8 LMS Evo", GT3),
    AMR_V8_VANTAGE(20, "AMR V8 Vantage", GT3),
    HONDA_NSX_GT3_EVO(21, "Honda NSX GT3 Evo", GT3),
    MCLAREN_720S_GT3(22, "McLaren 720S GT3", GT3),
    PORSCHE_911_II_GT3_R(23, "Porsche 911 II GT3 R", GT3),
    FERRARI_488_GT3_EVO(24, "Ferrari 488 GT3 Evo", GT3),
    MERCEDES_AMG_GT3_2020(25, "Mercedes-AMG GT3", GT3),
    ALPINE_A110_GT4(50, "Alpine A110 GT4", GT4),
    // GT4's
    ASTON_MARTIN_VANTAGE_GT4(51, "Aston Martin Vantage GT4", GT4),
    AUDI_R8_LMS_GT4(52, "Audi R8 LMS GT4", GT4),
    BMW_M4_GT4(53, "BMW M4 GT4", GT4),
    CHEVROLET_CAMARO_GT4(55, "Chevrolet Camaro GT4", GT4),
    GINETTA_G55_GT4(56, "Ginetta G55 GT4", GT4),
    KTM_X_BOW_GT4(57, "KTM X-Bow GT4", GT4),
    MASERATI_MC_GT4(58, "Maserati MC GT4", GT4),
    MCLAREN_570S_GT4(59, "McLaren 570S GT4", GT4),
    MERCEDES_AMG_GT4(60, "Mercedes AMG GT4", GT4),
    PORSCHE_718_CAYMAN_GT4_CLUBSPORT(61, "Porsche 718 Cayman GT4 Clubsport", GT4),
    ERROR(999, "ERROR", NONE);

    private final int type;
    private final String name;
    private final CarCategory category;

    private CarModel(int type, String name, CarCategory category) {
        this.type = type;
        this.name = name;
        this.category = category;
    }

    public static CarModel fromType(int id) {
        for (CarModel value : CarModel.values()) {
            if (value.getType() == id) {
                return value;
            }
        }
        return ERROR;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public CarCategory getCategory() {
        return category;
    }
}
