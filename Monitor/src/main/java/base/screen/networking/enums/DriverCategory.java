
package base.screen.networking.enums;

/**
 *
 * @author Leonard
 */
public enum DriverCategory {

    BRONZE(0),
    SILVER(1),
    GOLD(2),
    PLATINUM(3),
    ERROR(255);

    private int id;

    private DriverCategory(int id) {
        this.id = id;
    }

    public static DriverCategory fromId(int id) {
        switch (id) {
            case 0:
                return BRONZE;
            case 1:
                return SILVER;
            case 2:
                return GOLD;
            case 3:
                return PLATINUM;
            default:
                return ERROR;
        }
    }
    
    public int getId(){
        return id;
    }

}
