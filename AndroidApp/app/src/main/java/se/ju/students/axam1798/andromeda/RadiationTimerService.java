package se.ju.students.axam1798.andromeda;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import se.ju.students.axam1798.andromeda.models.User;

public class RadiationTimerService extends Service {

    private final static String TAG = RadiationTimerService.class.getName();

    private final static double TEMP_RADIATION_EXPOSURE = 30;
    private final static double TEMP_ROOM_COEFFICIENT = 0.2;
    private final static double TEMP_PROTECTIVE_COEFFICIENT = 2;

    private UserManager m_userManager;

    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;

    public RadiationTimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.m_userManager = UserManager.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        startTimer();
        return START_STICKY;
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer to run every 1 second
        timer.schedule(timerTask,1000,1000);
    }

    // prints counter every second
    public void initializeTimerTask() {
        if(m_userManager.getUser() == null) {
            Log.i(TAG, "Stored user is null, timer not started");
            return;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "in timer ++++++ " + (counter++));
                double safetyLimit = m_userManager.getUser().getSafetyLimit();
                safetyLimit -= m_userManager.getUser().getCurrentRadiationExposure(
                        // TODO: Should be fetched from hardware
                        TEMP_RADIATION_EXPOSURE, // TODO HardwareManager.getCurrentExposure()
                        TEMP_ROOM_COEFFICIENT, // TODO m_userManager.getUser().getRoomCoefficient()
                        m_userManager.getUser().getProtectiveCoefficient()
                );
                m_userManager.getUser().setSafetyLimit(safetyLimit);
                Log.i(TAG,"Limit is: " + (m_userManager.getUser().getSafetyLimit()));
            }
        };
    }

    //Stop the timer if it's not already null
    public void stopTimerTask() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
