package se.ju.students.axam1798.andromeda;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RadiationTimerService extends Service {

    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;

    public RadiationTimerService(Context applicationContext) {
        super();
        Log.i("This is the","RadiationTimerService");
    }

    public RadiationTimerService(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT","onDestroy!");
        Intent broadcastIntent = new Intent(this,RadiationTimerRestarterBroadcastReciever.class);

        sendBroadcast(broadcastIntent);
        stopTimerTask();
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
