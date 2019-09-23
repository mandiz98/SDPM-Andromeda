package se.ju.students.axam1798.andromeda;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1 = "channel1";

    //runs and creates channels as soon as app starts
    @Override
    public void onCreate(){
        super.onCreate();

        createNotificaitonChannels();
    }

    //Create one channel for each notification
    private void createNotificaitonChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1,
                    "warning",
                    NotificationManager.IMPORTANCE_HIGH
            );
            //Customize notification(s)
            channel1.setDescription("Warning: Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            //Create channel
            manager.createNotificationChannel(channel1);
        }

    }
}
