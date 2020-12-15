/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.networking.enums;

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
        switch (id) {
            case 0:
                return ANY;
            case 1:
                return ITALY;
            case 2:
                return GERMANY;
            case 3:
                return FRANCE;
            case 4:
                return SPAIN;
            case 5:
                return GREATBRITAIN;
            case 6:
                return HUNGARY;
            case 7:
                return BELGIUM;
            case 8:
                return SWITZERLAND;
            case 9:
                return AUSTRIA;
            case 10:
                return RUSSIA;
            case 11:
                return THAILAND;
            case 12:
                return NETHERLANDS;
            case 13:
                return POLAND;
            case 14:
                return ARGENTINA;
            case 15:
                return MONACO;
            case 16:
                return IRELAND;
            case 17:
                return BRAZIL;
            case 18:
                return SOUTHAFRICA;
            case 19:
                return PUERTORICO;
            case 20:
                return SLOVAKIA;
            case 21:
                return OMAN;
            case 22:
                return GREECE;
            case 23:
                return SAUDIARABIA;
            case 24:
                return NORWAY;
            case 25:
                return TURKEY;
            case 26:
                return SOUTHKOREA;
            case 27:
                return LEBANON;
            case 28:
                return ARMENIA;
            case 29:
                return MEXICO;
            case 30:
                return SWEDEN;
            case 31:
                return FINLAND;
            case 32:
                return DENMARK;
            case 33:
                return CROATIA;
            case 34:
                return CANADA;
            case 35:
                return CHINA;
            case 36:
                return PORTUGAL;
            case 37:
                return SINGAPORE;
            case 38:
                return INDONESIA;
            case 39:
                return USA;
            case 40:
                return NEWZEALAND;
            case 41:
                return AUSTRALIA;
            case 42:
                return SANMARINO;
            case 43:
                return UAE;
            case 44:
                return LUXEMBOURG;
            case 45:
                return KUWAIT;
            case 46:
                return HONGKONG;
            case 47:
                return COLOMBIA;
            case 48:
                return JAPAN;
            case 49:
                return ANDORRA;
            case 50:
                return AZERBAIJAN;
            case 51:
                return BULGARIA;
            case 52:
                return CUBA;
            case 53:
                return CZECHREPUBLIC;
            case 54:
                return ESTONIA;
            case 55:
                return GEORGIA;
            case 56:
                return INDIA;
            case 57:
                return ISRAEL;
            case 58:
                return JAMAICA;
            case 59:
                return LATVIA;
            case 60:
                return LITHUANIA;
            case 61:
                return MACAU;
            case 62:
                return MALAYSIA;
            case 63:
                return NEPAL;
            case 64:
                return NEWCALEDONIA;
            case 65:
                return NIGERIA;
            case 66:
                return NORTHERNIRELAND;
            case 67:
                return PAPUANEWGUINEA;
            case 68:
                return PHILIPPINES;
            case 69:
                return QATAR;
            case 70:
                return ROMANIA;
            case 71:
                return SCOTLAND;
            case 72:
                return SERBIA;
            case 73:
                return SLOVENIA;
            case 74:
                return TAIWAN;
            case 75:
                return UKRAINE;
            case 76:
                return VENEZUELA;
            case 77:
                return WALES;
            default:
                return ANY;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
