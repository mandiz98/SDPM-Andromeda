package se.ju.students.axam1798.andromeda;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManagerCompat;

    private UserManager m_userManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        m_userManager = UserManager.getInstance(context);

        notificationManagerCompat = NotificationManagerCompat.from(context);

        //TODO Get values from safety console
        double radPerSec = m_userManager.getUser().getCurrentRadiationExposure(
                30,
                0.2,
                 m_userManager.getUser().getProtectiveCoefficient()
        );

        Date dateNow = new Date();
        Date dateExpires = getTimeLeft(radPerSec, m_userManager.getUser().getSafetyLimit());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        long diff = Math.abs(dateExpires.getTime() - dateNow.getTime());
        int hours = (int)Math.floor(diff / 3.6e6);
        int minutes = (int)Math.floor((diff % 3.6e6) / 6e4);
        int seconds = (int)Math.floor((diff % 6e4) / 1000);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setContentTitle("Time left: " + hours + ":" + minutes + ":" + seconds)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Send it
        notificationManagerCompat.notify(2, notification);

    }

    private Date getTimeLeft(double radiationPerSec, double radiationLimit){

        double timeLeft = radiationLimit/radiationPerSec;

        long unixTime = System.currentTimeMillis() / 1000L;

        long intTimeLeft = (int) timeLeft + unixTime;

        return new Date(intTimeLeft*1000);
    }

}
