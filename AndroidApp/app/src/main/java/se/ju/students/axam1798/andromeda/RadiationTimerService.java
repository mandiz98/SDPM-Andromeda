package se.ju.students.axam1798.andromeda;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;

public class RadiationTimerService extends Service {

    private final static String TAG = RadiationTimerService.class.getName();

    private static double m_rads = 0;
    private final static int INTERVAL_SECONDS = 1800;

    private int m_accumilator = 30;

    private UserManager m_userManager;
    private NotificationManagerCompat m_notificationManagerCompat;

    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    private long m_alertTimestamp;

    private NotificationCompat.Builder m_warningNotificationBuilder;

    private IBinder binder = new LocalBinder();

    public RadiationTimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.m_userManager = UserManager.getInstance(getApplicationContext());
        this.m_alertTimestamp = 0;
        this.m_notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        this.m_warningNotificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setContentTitle("Time left until safety limit reached");
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

    public double getTimeLeft(){
        double safetyLimit = m_userManager.getUser().getSafetyLimit();

        return (safetyLimit / m_rads) * 1000;
    }

    public String getHourString(double timeLeft) {
        int hours = (int)Math.floor(timeLeft / 3.6e6);
        return hours >= 10 ? String.valueOf(hours) : "0" + hours;
    }

    public String getMinuteString(double timeLeft) {
        int minutes = (int)Math.floor((timeLeft % 3.6e6) / 6e4);
        return minutes >= 10 ? String.valueOf(minutes) : "0" + minutes;
    }

    public String getSecondString(double timeLeft) {
        int seconds = (int)Math.floor((timeLeft % 6e4) / 1000);
        return seconds >= 10 ? String.valueOf(seconds) : "0" + seconds;
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
                double safetyLimit = m_userManager.getUser().getSafetyLimit();

                safetyLimit -= m_rads;

                double timeLeft = getTimeLeft();
                String notificationText = "Expires in " +
                        getHourString(timeLeft) + ":" +
                        getMinuteString(timeLeft) + ":" +
                        getSecondString(timeLeft);
                boolean alert = false;

                {
                    m_accumilator++;
                    if(m_accumilator == 30)
                    {
                        m_accumilator = 0;

                        // Send bluetooth message to console
                        String time = getHourString(timeLeft) + ":" +
                                getMinuteString(timeLeft) + ":" +
                                getSecondString(timeLeft);
                        BluetoothProtocolParser.Statement statement = new BluetoothProtocolParser.Statement();
                        statement.eventKey = 3003;
                        statement.data = time; // At most 16 chars for console limit
                        String msg = BluetoothProtocolParser.parse(statement);
                        MessageQueue.getInstance().pushMessage(
                                MessageQueue.MESSAGE_TYPE.SEND_BLUETOOTH,
                                msg
                        );
                    }
                }

                // If first time, or each 30 minutes
                if(((m_alertTimestamp == 0) ||
                    (System.currentTimeMillis() - m_alertTimestamp) / 1000 >= INTERVAL_SECONDS) &&
                    Math.floor(safetyLimit) > 0)
                {
                    // Make the notification trigger an alert
                    alert = true;

                }else if(Math.floor(safetyLimit) <= 0) {
                    // Show warning that safety limit reached
                    notificationText = "You should really leave the power plant, no safety limit left";
                    alert = true;
                    stopTimerTask();

                    // Send bluetooth message to console
                    BluetoothProtocolParser.Statement statement = new BluetoothProtocolParser.Statement();
                    statement.eventKey = 3002;
                    statement.data = "GET OUT!"; // At most 16 chars for console limit
                    String msg = BluetoothProtocolParser.parse(statement);
                    MessageQueue.getInstance().pushMessage(
                            MessageQueue.MESSAGE_TYPE.SEND_BLUETOOTH,
                            msg
                    );
                }

                // Trigger the notification
                showWarningNotification(
                        notificationText,
                        alert
                );

                m_userManager.getUser().setSafetyLimit(safetyLimit);
                Log.i(TAG,"Current safety limit: " + (m_userManager.getUser().getSafetyLimit()));
                //Log.i(TAG,"Left until reached limit: " + strHours + ":" + strMinutes + ":" + strSeconds);
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

    private void showWarningNotification(String message, boolean alert) {
        m_warningNotificationBuilder.setContentText(message);
        m_warningNotificationBuilder.setOnlyAlertOnce(!alert);
        if(alert) {
            m_notificationManagerCompat.cancel(2);
            m_alertTimestamp = System.currentTimeMillis();
        }
        m_notificationManagerCompat.notify(2, m_warningNotificationBuilder.build());
    }

    public void setRads(double rads) {
        m_rads = rads;
    }

    public class LocalBinder extends Binder {
        public RadiationTimerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RadiationTimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}

