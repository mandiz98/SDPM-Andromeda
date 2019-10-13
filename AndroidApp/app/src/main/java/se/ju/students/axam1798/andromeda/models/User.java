package se.ju.students.axam1798.andromeda.models;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APICallback;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.API.APIError;
import se.ju.students.axam1798.andromeda.UserManager;
import se.ju.students.axam1798.andromeda.enums.Role;
import se.ju.students.axam1798.andromeda.fragments.ClockedIn;

public class User {

    private static final int SAFETY_LIMIT_START = 500000;

    private int id;
    private String rfid;
    private boolean clockedIn;
    private boolean hazmatSuite;
    @SerializedName("roleId")
    private Role role;
    double rads;

    private double m_safetyLimit;

    public User() {
        this.m_safetyLimit = SAFETY_LIMIT_START;
    }

    public User(int id, String rfid, boolean clockedIn, boolean hazmatSuite, Role role) {
        this.id = id;
        this.rfid = rfid;
        this.clockedIn = clockedIn;
        this.hazmatSuite = hazmatSuite;
        this.role = role;
        this.m_safetyLimit = SAFETY_LIMIT_START;
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

    public Role getRole() {
        return role;
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
