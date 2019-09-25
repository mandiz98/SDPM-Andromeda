package se.ju.students.axam1798.andromeda;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;

import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.models.User;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothService m_bluetoothService = null;
    private BluetoothProtocolParser m_parser = new BluetoothProtocolParser();
    private BluetoothService.BluetoothConnection m_connection = null;

    private NotificationManagerCompat notificationManagerCompat;

    private Button m_TestBtBtn;
    final Handler m_Handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        m_Handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    sendIntervalWarningNotification();
                } catch(NullPointerException e){
                    System.out.println("Caught the NullPointerException");
                }
            }
        }, 180);

        m_TestBtBtn = findViewById(R.id.test_bt_btn);
        m_TestBtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clockIn();
                //test everything
                sendWarningNotification(v);
                sendSystemWideWarning();
                systemWideWarningAlert();

            }
        });
        
        m_bluetoothService = new BluetoothService();
        if(m_bluetoothService.isSupported())
        {
            if(!m_bluetoothService.isEnabled())
            {
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);
            }
            else
            {
                setupBTConnection();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setupBTConnection();
    }

    public void setupBTConnection()
    {
        m_connection = m_bluetoothService.connect(
            m_bluetoothService.getPairedDevice("HC-06"),
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if(msg.what == BluetoothService.BluetoothConnection.MESSAGE_READ)
                    {
                        String readMessage = null;
                        try
                        {
                            readMessage = (String)msg.obj;

                            BluetoothProtocolParser.Statement statement = m_parser.parse(readMessage);
                            if(statement.isComplete)
                            {
                                Log.i(TAG, new String("EVENTKEY:").concat(Integer.toString(statement.eventKey)));
                                Log.i(TAG, new String("TIMESTAMP:").concat(Long.toString(statement.timestamp)));
                                Log.i(TAG, new String("DATA:").concat(statement.data));
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        );

        m_connection.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        m_connection.cancel();
    }

    //Create the notification
    public void sendWarningNotification(View v){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setContentTitle("Warning 1")
                .setContentText("Warning 1")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Send it
        notificationManagerCompat.notify(1, notification);
        /* TODO: Remove
         * Example code to make an API call
        User user = new User(0, UUID.randomUUID().toString(), false, false);
        APIClient.getInstance().createUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null)
                    Toast.makeText(getApplicationContext(), response.body().getRFID(), Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(getApplicationContext(), APIClient.decodeError(response.errorBody()).getMessage() + "<- error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
         */
    }

    public void sendIntervalWarningNotification(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setContentTitle("Time interval")
                .setContentText("Time interval")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Send it
        notificationManagerCompat.notify(2, notification);
    }

    public void sendSystemWideWarning(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1)
                .setSmallIcon(R.drawable.ic_warning_1)
                .setContentTitle("System wide warning")
                .setContentText("System wide warning")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Send it
        notificationManagerCompat.notify(3, notification);
    }

    public void systemWideWarningAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("SystemWideWarning");
        alertDialog.setMessage("SystemWideWarning");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //Go to clocked in fragment
    private void clockIn() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockedIn());
        fragmentManager.commit(); }

}

