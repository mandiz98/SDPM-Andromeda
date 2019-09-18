package se.ju.students.axam1798.andromeda.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String rfid;

    @DatabaseField()
    private boolean clockedIn;

    @DatabaseField()
    private boolean hazmatSuite;

    public User() {}

    public User(int id, String rfid, boolean clockedIn, boolean hazmatSuite) {
        this.id = id;
        this.rfid = rfid;
        this.clockedIn = clockedIn;
        this.hazmatSuite = hazmatSuite;
    }

    /**
     * Calculate the current radiation exposure unit per second
     * @param reactorRadiation Reactor radiation units output per second
     * @param roomCoefficient The room's protective coefficient
     * @param protectiveCoefficient Like clothes, hazmatsuite etc.
     * @return current radiation exposure unit per second
     */
    public double getCurrentRadiationExposure(double reactorRadiation, double roomCoefficient, double protectiveCoefficient) {
        return  (reactorRadiation * roomCoefficient) / protectiveCoefficient;
    }
}
