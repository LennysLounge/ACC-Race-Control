/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol.enums;

import static racecontrol.client.protocol.enums.CarCategory.CUP;
import static racecontrol.client.protocol.enums.CarCategory.CUP21;
import static racecontrol.client.protocol.enums.CarCategory.GT3;
import static racecontrol.client.protocol.enums.CarCategory.GT4;
import static racecontrol.client.protocol.enums.CarCategory.NONE;
import static racecontrol.client.protocol.enums.CarCategory.ST;
import static racecontrol.client.protocol.enums.CarCategory.ST22;
import static racecontrol.client.protocol.enums.CarCategory.TCX;
import static racecontrol.client.protocol.enums.CarCategory.CHL;

/**
 * The different car models in the game.
 *
 * @author Leonard
 */
public enum CarModel {
    // GT3's
    PORSCHE_991_GT3_R(0, "Porsche 991 GT3 R", "Porsche", GT3),
    MERCEDES_AMG_GT3_2015(1, "Mercedes-AMG GT3", "Mercedes-AMG", GT3),
    FERRARI_488_GT3(2, "Ferrari 488 GT3", "Ferrari", GT3),
    AUDI_R8_LMS(3, "Audi R8 LMS", "Audi", GT3),
    LAMBORGHINI_HURACAN_GT3(4, "Lamborghini Huracan GT3", "Lamborghini", GT3),
    MCLAREN_650S_GT3(5, "McLaren 650S GT3", "McLaren", GT3),
    NISSAN_GT_R_NISMO_GT3_2018(6, "Nissan GT-R Nismo GT3 2018", "Nissan", GT3),
    BMW_M6_GT3(7, "BMW M6 GT3", "BMW", GT3),
    BENTLEY_CONTINENTAL_GT3_2018(8, "Bentley Continental GT3 2018", "Bentley", GT3),
    PORSCHE_991_II_GT3_CUP(9, "Porsche 991 II GT3 Cup", "Porsche", CUP),
    NISSAN_GT_R_NISMO_GT3_2015(10, "Nissan GT-R Nismo GT3", "Nissan", GT3),
    BENTLEY_CONTINENTAL_GT3_2015(11, "Bentley Continental GT3", "Bentley", GT3),
    AMR_V12_VANTAGE_GT3(12, "AMR V12 Vantage GT3", "Aston-Martin", GT3),
    REITER_ENGINEERING_R_EX_GT3(13, "Reiter Engineering R-EX GT3", "Reiter-Engineering", GT3),
    EMIL_FREY_JAGUAR_G3(14, "Emil Frey Jaguar G3", "Jaguar", GT3),
    LEXUS_RC_F_GT3(15, "Lexus RC F GT3", "Lexus", GT3),
    LAMBORGHINI_HURACAN_GT3_EVO(16, "Lamborghini Huracan GT3 Evo", "Lamborghini", GT3),
    HONDA_NSX_GT3(17, "Honda NSX GT3", "Honda", GT3),
    LAMBORGHINI_HURACAN_ST(18, "Lamborghini Huracan ST", "Lamborghini", ST),
    AUDI_R8_LMS_EVO(19, "Audi R8 LMS Evo", "Audi", GT3),
    AMR_V8_VANTAGE(20, "AMR V8 Vantage", "Aston-Martin", GT3),
    HONDA_NSX_GT3_EVO(21, "Honda NSX GT3 Evo", "Honda", GT3),
    MCLAREN_720S_GT3(22, "McLaren 720S GT3", "McLaren", GT3),
    PORSCHE_911_II_GT3_R(23, "Porsche 911 II GT3 R", "Porsche", GT3),
    FERRARI_488_GT3_EVO(24, "Ferrari 488 GT3 Evo", "Ferrari", GT3),
    MERCEDES_AMG_GT3_2020(25, "Mercedes-AMG GT3 2020", "Mercedes-AMG", GT3),
    FERRARI_488_CHALLENGE_EVO(26, "Ferrari 488 Challenge Evo", "Ferrari", CHL),
    BMW_M2_CS_RACING(27, "BMW M2 CS Racing", "BMW", TCX),
    PORSCHE_(28, "Porsche 992 GT3 CUP", "Porsche", CUP21),
    LAMBORGHINI_HURACAN_ST_EVO2(29, "Lamborghini Huracan ST EVO2", "Lamborghini", ST22),
    BMW_M4_GT3(30, "BMW M4 GT3", "BMW", GT3),
    AUDI_R8_LMS_EVO2(31, "Audi R8 LMS Evo 2", "Audi", GT3),
    FERRARI_296_GT3(32, "FERRARI 296 GT3", "Ferrari", GT3),
    LAMBORGHINI_HURACAN_EVO2(33, "Lamborghini Huracan EVO2", "Lamborghini", GT3),
    PORSCHE_992_GT3_R(34, "Porsche 992 GT3 R", "Porsche", GT3),
    MCLAREN_720S_GT3_EVO(35, "McLaren 720S GT3 Evo", "McLaren", GT3),
    // GT4's
    ALPINE_A110_GT4(50, "Alpine A110 GT4", "Alpine", GT4),
    ASTON_MARTIN_VANTAGE_GT4(51, "Aston Martin Vantage GT4", "Aston-Martin", GT4),
    AUDI_R8_LMS_GT4(52, "Audi R8 LMS GT4", "Audi", GT4),
    BMW_M4_GT4(53, "BMW M4 GT4", "BMW", GT4),
    CHEVROLET_CAMARO_GT4(55, "Chevrolet Camaro GT4", "Chevrolet", GT4),
    GINETTA_G55_GT4(56, "Ginetta G55 GT4", "Ginetta", GT4),
    KTM_X_BOW_GT4(57, "KTM X-Bow GT4", "KTM", GT4),
    MASERATI_MC_GT4(58, "Maserati MC GT4", "Maserati", GT4),
    MCLAREN_570S_GT4(59, "McLaren 570S GT4", "McLaren", GT4),
    MERCEDES_AMG_GT4(60, "Mercedes AMG GT4", "Mercedes-AMG", GT4),
    PORSCHE_718_CAYMAN_GT4_CLUBSPORT(61, "Porsche 718 Cayman GT4 Clubsport", "Porsche", GT4),
    ERROR(999, "ERROR", "Error", NONE);

    private final int type;
    private final String name;
    private final String constructor;
    private final CarCategory category;

    private CarModel(int type, String name, String constructor, CarCategory category) {
        this.type = type;
        this.name = name;
        this.constructor = constructor;
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

    public String getConstructor() {
        return constructor;
    }

    @Override
    public String toString() {
        return "CarModel{"
                + "type=" + type
                + ", name=" + name
                + ", constructor=" + constructor
                + ", category=" + category
                + '}';
    }

}
