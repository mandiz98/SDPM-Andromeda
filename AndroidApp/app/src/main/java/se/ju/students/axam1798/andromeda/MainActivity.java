package se.ju.students.axam1798.andromeda;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static se.ju.students.axam1798.andromeda.App.CHANNEL_1;

import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APICallback;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.API.APIError;
import se.ju.students.axam1798.andromeda.enums.Role;
import se.ju.students.axam1798.andromeda.fragments.ClockOut;
import se.ju.students.axam1798.andromeda.fragments.ClockedIn;
import se.ju.students.axam1798.andromeda.exceptions.NotPairedException;
import se.ju.students.axam1798.andromeda.models.Event;

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
    private Intent m_serviceIntent;
    private Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_context = this;

        m_userManager = UserManager.getInstance(getApplicationContext());
        notificationManagerCompat = NotificationManagerCompat.from(this);
        m_serviceIntent = new Intent(getApplicationContext(), RadiationTimerService.class);

        m_bluetoothService = new BluetoothService();

        //Bluetooth connection animation
        final ImageView rfidAnimation = findViewById(R.id.img_rfid);
        rfidAnimation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setImageAnimate();

                setupBTConnection();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(
            requestCode == 0 &&
            resultCode == RESULT_OK
        ) {
            finishBTConnection();
        }
    }

    private void finishBTConnection()
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

                                    if(user.isClockedIn()) {
                                        clockIn();
                                    }
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
                                m_userManager.getUser() != null ? m_userManager.getUser().getId() : 0,
                                statement.eventKey,
                                new Date(),
                                statement.data
                        ),
                        createEventCallback // callback for when the "create event" call to the API finished
                );
            }
        };

        try {
            m_connection = m_bluetoothService.connect(
                    m_bluetoothService.getPairedDevice("HC-06"),
                    new BluetoothMessageHandler(m_parser, listener)
            );

            m_connection.setCallbacks(new BluetoothService.ConnectionCallbacks() {
                @Override
                public void onConnect(boolean success) {
                    if(success)
                    {
                        final BluetoothService.BluetoothConnection temp = m_connection;
                        try
                        {
                            BluetoothProtocolParser.Statement statement = new BluetoothProtocolParser.Statement();
                            statement.eventKey = 3004;
                            statement.data = "Phone connected!";
                            byte[] msg = m_parser.parse(statement).getBytes();
                            m_connection.write(
                                    msg
                            );
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.getMessage());
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                setImageConnected();
                                fetchStoredUserInfo();
                            }
                        });
                    }
                    else
                    {
                        disconnect();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                setImageDisconnected();
                            }
                        });
                    }
                }
            });

            m_connection.start();
        } catch (NotPairedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            m_connection.cancel();
        }
    }

    public void setupBTConnection()
    {
        if(m_bluetoothService.isSupported())
        {
            if(!m_bluetoothService.isEnabled())
            {
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);
            }
            else
            {
                finishBTConnection();
            }
        }
        // TODO: Error bluetooth not supported
    }

    public void disconnect()
    {
        m_connection.cancel();
        m_connection = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stopService(m_serviceIntent);
        Log.i("MAINACT","onDestroy!");
        disconnect();
    }

    public void setImageDisconnected() {
        final ImageView rfidImage = findViewById(R.id.img_rfid);
        rfidImage.setBackgroundResource(R.drawable.ic_rfid_disconnected);
    }


    public void setImageAnimate() {
        final ImageView rfidImage = findViewById(R.id.img_rfid);
        rfidImage.setBackgroundResource(R.drawable.rfid_animation);
        ((AnimationDrawable)rfidImage.getBackground()).start();
    }


    public void setImageConnected() {
        final ImageView rfidImage = findViewById(R.id.img_rfid);
        rfidImage.setBackgroundResource(R.drawable.ic_rfid);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isServiceRunning?", false+"");
        return false;
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
    private void clockIn() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockedIn());
        fragmentManager.commit();

        if (!isServiceRunning(RadiationTimerService.class)){
            startService(m_serviceIntent);
        }

        // Send message to do a success sound to the console
        m_connection.write(m_parser.parse(new BluetoothProtocolParser.Statement(
                3000,
                System.currentTimeMillis()
        )).getBytes());
    }

    //Go to clocked out fragment
    private void clockOut(){
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockOut());
        fragmentManager.commit();

        if (!isServiceRunning(RadiationTimerService.class)){
            stopService(m_serviceIntent);
        }

        // Send message to do a fail sound to the console
        m_connection.write(m_parser.parse(new BluetoothProtocolParser.Statement(
                3001,
                System.currentTimeMillis()
        )).getBytes());
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

    private void fetchStoredUserInfo() {
        // Get stored user after bluetooth connection is established
        if(m_userManager.getUser() != null) {
            // Get current user data from API
            APIClient.getInstance().getUserById(m_userManager.getUser().getId(), new APICallback<User>(m_context) {
                @Override
                public void onSuccess(Call<User> call, Response<User> response, User user) {
                    if(user == null) {
                        m_userManager.setStoredUser(null);
                        return;
                    }
                    // Store the user
                    m_userManager.setStoredUser(user);

                    // Show clock in page if clocked in
                    if(user.isClockedIn())
                        clockIn();
                }

                @Override
                public void onError(Call<User> call, Response<User> response, APIError error) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Couldn't get stored user from API: " + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }
}

