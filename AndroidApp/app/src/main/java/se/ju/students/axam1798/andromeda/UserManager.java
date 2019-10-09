package se.ju.students.axam1798.andromeda;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import se.ju.students.axam1798.andromeda.models.User;


public class UserManager {

    private final static String PREFS_NAME = "userPreferences";
    private final static String KEY_USER = "user";
    private static UserManager instance;

    private SharedPreferences m_sharedPrefs;
    private User m_instancedUser;

    private UserManager(Context context) {
        this.m_sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.m_instancedUser = null;
    }

    public static UserManager getInstance(Context context){
        if(instance == null){
            instance = new UserManager(context);
        }
        instance.m_sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return instance;
    }

    public User getUser() {
        if(m_instancedUser != null) {
            return m_instancedUser;
        }

        String userJson = m_sharedPrefs.getString(KEY_USER, null);
        if(userJson == null)
            return null;
        Gson gson = new Gson();
        m_instancedUser = gson.fromJson(userJson, User.class);
        return m_instancedUser;
    }

    public void setStoredUser(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        m_sharedPrefs.edit()
                .putString(KEY_USER, userJson)
                .apply();
        m_instancedUser = user;
    }

    public double getTimeLeft(double radiationExposure, double roomCoefficient, double protectiveCoefficient) {
        double safetyLimit = getUser().getSafetyLimit();
        double currentExposurePerSecond = getUser().getCurrentRadiationExposure(
                // TODO: Should be fetched from hardware
                radiationExposure, // TODO HardwareManager.getCurrentExposure()
                roomCoefficient,
                protectiveCoefficient
        );

        return (safetyLimit / currentExposurePerSecond) * 1000;
    }
}
