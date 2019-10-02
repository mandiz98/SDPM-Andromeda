package se.ju.students.axam1798.andromeda;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onReceive(Context context, Intent intent) {

        notificationManagerCompat = NotificationManagerCompat.from(context);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setContentTitle("Time left: X")
                .setContentText("Time left: X")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Send it
        notificationManagerCompat.notify(2, notification);

    }

}
