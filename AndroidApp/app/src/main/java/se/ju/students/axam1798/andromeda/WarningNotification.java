package se.ju.students.axam1798.andromeda;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class WarningNotification extends Notification {

    String CHANNEL_ID = "987654321";
    String ntfTitle = "Warning";
    String ntfDesc = "This is a warning";
    private Context m_context;


    public WarningNotification(Context context){
        this.m_context = context;
    }

    public Notification build(){
        NotificationCompat.Builder m_builder = new NotificationCompat.Builder(m_context,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_andromeda)
                .setContentTitle(ntfTitle)
                .setContentText(ntfDesc)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return m_builder.build();
    }
}
