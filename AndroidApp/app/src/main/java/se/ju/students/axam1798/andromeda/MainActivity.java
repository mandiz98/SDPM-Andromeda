package se.ju.students.axam1798.andromeda;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;

import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.API.APIError;
import se.ju.students.axam1798.andromeda.models.Event;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import se.ju.students.axam1798.andromeda.models.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothService m_bluetoothService = null;
    private BluetoothProtocolParser m_parser = new BluetoothProtocolParser();
    private BluetoothService.BluetoothConnection m_connection = null;

    private NotificationManagerCompat notificationManagerCompat;

    private UserManager m_userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_userManager = new UserManager(getApplicationContext());
        notificationManagerCompat = NotificationManagerCompat.from(this);

        // Get stored user
        if(m_userManager.getUser() != null) {
            // Get current user data from API
            APIClient.getInstance().getUserById(m_userManager.getUser().getId(), new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        // Get the user from the response body
                        User user = response.body();
                        // Store the user
                        m_userManager.setStoredUser(user);

                        // Show clock in page if clocked in
                        if(!user.isClockedIn())
                            showClockInFragment();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Couldn't get stored user from API: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }

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
        // Callback from API after the "create event" request was finished
        final Callback<Event> createEventCallback = new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                String toastMessage;
                if(response.isSuccessful() && response.body() != null) {
                    // Successful response! Event created.
                    Event eventCreated = response.body();
                    toastMessage = String.format(
                            Locale.getDefault(),
                            "Stored event %d with data \"%s\" on %s",
                            eventCreated.getEventKey(),
                            eventCreated.getData(),
                            eventCreated.getDateCreated()
                    );

                    // eventKey 4010 = clock in/out, get user's clocked in status
                    if(eventCreated.getEventKey() == 4010) {
                        APIClient.getInstance().getUserByRfid(eventCreated.getData(), new retrofit2.Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    // Get the user from the response body
                                    User user = response.body();
                                    // Store the user
                                    m_userManager.setStoredUser(user);

                                    if(!user.isClockedIn())
                                        showClockInFragment();
                                    else
                                        showClockOutFragment();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Couldn't get user by rfid: " + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    }
                }else if(response.errorBody() != null){
                    // Not successful, we got an error body
                    APIError error = APIClient.decodeError(response.errorBody());
                    toastMessage = error.getMessage();
                }else{
                    // No error body in response, but connection to server was successful
                    toastMessage = "Unknown error (HTTP code "+response.code()+")";
                }

                // Display a toast! :^)
                Toast.makeText(
                        getApplicationContext(),
                        toastMessage,
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "Couldn't store event, connection to API failed",
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        // Triggered when we receive a correct event from the bluetooth device
        IBluetoothEventListener listener = new IBluetoothEventListener() {
            @Override
            public void onReceived(BluetoothProtocolParser.Statement statement) {
                APIClient.getInstance().createEvent(
                        new Event(
                                0,
                                0,
                                statement.eventKey,
                                new Date(),
                                statement.data
                        ),
                        createEventCallback // callback for when the "create event" call to the API finished
                );
            }
        };

        m_connection = m_bluetoothService.connect(
                m_bluetoothService.getPairedDevice("HC-06"),
                new BluetoothMessageHandler(m_parser, listener)
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
    }

    public void sendIntervalWarningNotification(View v){
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

    public void sendSystemWideWarning(View v){
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
    private void showClockInFragment() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockedIn());
        fragmentManager.commit();
    }

    //Go to clocked out fragment
    private void showClockOutFragment(){
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockOut());
        fragmentManager.commit();
    }

    /**
     * Bluetooth message handler to trigger the listener's
     * onReceived function when an event was sent from the bluetooth device.
     */
    static class BluetoothMessageHandler extends Handler {

        private final BluetoothProtocolParser m_parser;
        private final IBluetoothEventListener m_listener;

        BluetoothMessageHandler(BluetoothProtocolParser bluetoothParser, IBluetoothEventListener listener) {
            this.m_parser = bluetoothParser;
            this.m_listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == BluetoothService.BluetoothConnection.MESSAGE_READ)
            {
                String readMessage = null;
                try
                {
                    readMessage = (String)msg.obj;

                    // Parse the statement into an object
                    BluetoothProtocolParser.Statement statement = m_parser.parse(readMessage);
                    if(statement.isComplete)
                    {
                        Log.i(TAG, new String("EVENTKEY:").concat(Integer.toString(statement.eventKey)));
                        Log.i(TAG, new String("TIMESTAMP:").concat(Long.toString(statement.timestamp)));
                        Log.i(TAG, new String("DATA:").concat(statement.data));

                        // Trigger the listener's onReceived function
                        this.m_listener.onReceived(statement);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

