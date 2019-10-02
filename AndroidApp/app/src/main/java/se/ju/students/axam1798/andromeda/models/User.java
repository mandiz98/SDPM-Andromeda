package se.ju.students.axam1798.andromeda.models;

public class User {
    private int id;
    private String rfid;
    private boolean clockedIn;
    private boolean hazmatSuite;

    private double m_safetyLimit;

    public User(int id, String rfid, boolean clockedIn, boolean hazmatSuite) {
        this.id = id;
        this.rfid = rfid;
        this.clockedIn = clockedIn;
        this.hazmatSuite = hazmatSuite;
        this.m_safetyLimit = 500000;
    }

    public int getId() {
        return id;
    }

    public String getRFID() {
        return rfid;
    }

    public boolean isClockedIn() {
        return clockedIn;
    }

    public boolean hasHazmatSuite() {
        return hazmatSuite;
    }

    public double getProtectiveCoefficient(){
        return hasHazmatSuite() ? 5 : 1;
    }

    public void setSafetyLimit(double safetyLimit) {
        this.m_safetyLimit = safetyLimit;
    }

    public double getSafetyLimit() {
        return m_safetyLimit;
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
