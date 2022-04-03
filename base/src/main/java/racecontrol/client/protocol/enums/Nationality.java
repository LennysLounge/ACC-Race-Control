/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol.enums;

/**
 *
 * @author Leonard
 */
public enum Nationality {

    ANY(0, "Any"),
    ITALY(1, "Italy"),
    GERMANY(2, "Germany"),
    FRANCE(3, "France"),
    SPAIN(4, "Spain"),
    GREATBRITAIN(5, "GreatBritain"),
    HUNGARY(6, "Hungary"),
    BELGIUM(7, "Belgium"),
    SWITZERLAND(8, "Switzerland"),
    AUSTRIA(9, "Austria"),
    RUSSIA(10, "Russia"),
    THAILAND(11, "Thailand"),
    NETHERLANDS(12, "Netherlands"),
    POLAND(13, "Poland"),
    ARGENTINA(14, "Argentina"),
    MONACO(15, "Monaco"),
    IRELAND(16, "Ireland"),
    BRAZIL(17, "Brazil"),
    SOUTHAFRICA(18, "SouthAfrica"),
    PUERTORICO(19, "PuertoRico"),
    SLOVAKIA(20, "Slovakia"),
    OMAN(21, "Oman"),
    GREECE(22, "Greece"),
    SAUDIARABIA(23, "SaudiArabia"),
    NORWAY(24, "Norway"),
    TURKEY(25, "Turkey"),
    SOUTHKOREA(26, "SouthKorea"),
    LEBANON(27, "Lebanon"),
    ARMENIA(28, "Armenia"),
    MEXICO(29, "Mexico"),
    SWEDEN(30, "Sweden"),
    FINLAND(31, "Finland"),
    DENMARK(32, "Denmark"),
    CROATIA(33, "Croatia"),
    CANADA(34, "Canada"),
    CHINA(35, "China"),
    PORTUGAL(36, "Portugal"),
    SINGAPORE(37, "Singapore"),
    INDONESIA(38, "Indonesia"),
    USA(39, "USA"),
    NEWZEALAND(40, "NewZealand"),
    AUSTRALIA(41, "Australia"),
    SANMARINO(42, "SanMarino"),
    UAE(43, "UAE"),
    LUXEMBOURG(44, "Luxembourg"),
    KUWAIT(45, "Kuwait"),
    HONGKONG(46, "HongKong"),
    COLOMBIA(47, "Colombia"),
    JAPAN(48, "Japan"),
    ANDORRA(49, "Andorra"),
    AZERBAIJAN(50, "Azerbaijan"),
    BULGARIA(51, "Bulgaria"),
    CUBA(52, "Cuba"),
    CZECHREPUBLIC(53, "CzechRepublic"),
    ESTONIA(54, "Estonia"),
    GEORGIA(55, "Georgia"),
    INDIA(56, "India"),
    ISRAEL(57, "Israel"),
    JAMAICA(58, "Jamaica"),
    LATVIA(59, "Latvia"),
    LITHUANIA(60, "Lithuania"),
    MACAU(61, "Macau"),
    MALAYSIA(62, "Malaysia"),
    NEPAL(63, "Nepal"),
    NEWCALEDONIA(64, "NewCaledonia"),
    NIGERIA(65, "Nigeria"),
    NORTHERNIRELAND(66, "NorthernIreland"),
    PAPUANEWGUINEA(67, "PapuaNewGuinea"),
    PHILIPPINES(68, "Philippines"),
    QATAR(69, "Qatar"),
    ROMANIA(70, "Romania"),
    SCOTLAND(71, "Scotland"),
    SERBIA(72, "Serbia"),
    SLOVENIA(73, "Slovenia"),
    TAIWAN(74, "Taiwan"),
    UKRAINE(75, "Ukraine"),
    VENEZUELA(76, "Venezuela"),
    WALES(7, "Wales");

    private int id;
    private String name;

    private Nationality(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Nationality fromId(int id) {
        for (var nation : Nationality.values()) {
            if (nation.getId() == id) {
                return nation;
            }
        }
        return ANY;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
