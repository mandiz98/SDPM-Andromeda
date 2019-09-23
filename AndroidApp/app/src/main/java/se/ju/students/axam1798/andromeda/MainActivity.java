package se.ju.students.axam1798.andromeda;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;


public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManagerCompat;

    private Button mTestBtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        mTestBtBtn = findViewById(R.id.test_bt_btn);
        mTestBtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockIn();
                sendChannel1(v);
            }
        });
    }

    //Create the notification
    public void sendChannel1(View v){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setContentTitle("warning 1")
                .setContentText("warning 1")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Send it
        notificationManagerCompat.notify(1, notification);
    }

    //Go to clocked in fragment
    private void clockIn() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockedIn());
        fragmentManager.commit(); }

}

