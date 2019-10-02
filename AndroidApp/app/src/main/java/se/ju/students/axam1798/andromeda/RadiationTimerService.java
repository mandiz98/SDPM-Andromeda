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

    private Context m_context;

    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    private double m_safetyLimit = 0;

    //TODO: Thesse variables should be fetched from the db.

    public RadiationTimerService(Context context) {
        Log.i("This is the","RadiationTimerService");
        this.m_context = context;
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
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("in timer", "in timer ++++++ " + (counter++));
                //TODO: reactorRadiation should be a variable from somewhere
                m_safetyLimit = m_userManager.getUser().getSafetyLimit(0.2);
                Log.i("safety limit","Limit is: " + (m_safetyLimit));
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



}
