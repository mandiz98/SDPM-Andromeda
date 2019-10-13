package se.ju.students.axam1798.andromeda.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APICallback;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.API.APIError;
import se.ju.students.axam1798.andromeda.BluetoothProtocolParser;
import se.ju.students.axam1798.andromeda.MainActivity;
import se.ju.students.axam1798.andromeda.MessageQueue;
import se.ju.students.axam1798.andromeda.R;
import se.ju.students.axam1798.andromeda.RadiationTimerService;
import se.ju.students.axam1798.andromeda.UserManager;
import se.ju.students.axam1798.andromeda.activities.EmployeeHistoryActivity;
import se.ju.students.axam1798.andromeda.activities.EmployeeListActivity;
import se.ju.students.axam1798.andromeda.enums.Role;
import se.ju.students.axam1798.andromeda.models.Event;
import se.ju.students.axam1798.andromeda.models.User;


public class ClockedIn extends Fragment {

    private Intent m_serviceIntent;
    private Context m_context;
    private RadiationTimerService m_service;
    private boolean m_bound = false;
    private TextView txtTimer;

    private final int DATABASE_DELAY = 200;

    private Handler timerTextHandler;

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(m_context, RadiationTimerService.class);
        m_context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RadiationTimerService.LocalBinder binder = (RadiationTimerService.LocalBinder) service;
            m_service = binder.getService();
            m_bound = true;
            updateTimerTextRunnable.run();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            m_bound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_clocked_in, container, false);
        m_context = view.getContext();
        m_serviceIntent = new Intent(getContext(), RadiationTimerService.class);
        timerTextHandler = new Handler();

        if (!isServiceRunning(RadiationTimerService.class)){
            m_context.startService(m_serviceIntent);
        }

        Toolbar toolbar = view.findViewById(R.id.clocked_in_toolbar);
        toolbar.inflateMenu(R.menu.menu_clocked_in);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menuitem_my_history: {
                        Bundle extras = new Bundle();
                        extras.putString(
                                EmployeeHistoryActivity.EMPLOYEE_KEY,
                                new Gson().toJson(UserManager.getInstance(getContext()).getUser())
                        );
                        Intent employeeHistoryIntent = new Intent(getContext(), EmployeeHistoryActivity.class);
                        employeeHistoryIntent.putExtras(extras);
                        startActivity(employeeHistoryIntent);
                        break;
                    }
                    case R.id.menuitem_employees: {
                        startActivity(new Intent(getContext(), EmployeeListActivity.class));
                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        });

        if (!isServiceRunning(RadiationTimerService.class)){
            m_context.startService(m_serviceIntent);
        }

        final UserManager userManager = UserManager.getInstance(getContext());
        if(userManager.getUser().getRole() == Role.MANAGER) {
            Menu menu = toolbar.getMenu();
            menu.getItem(1).setVisible(true); // Employees
        }

        //Update texts in fragment
        //Time left timer
        double timeLeft = UserManager.getInstance(view.getContext()).getTimeLeft(
                30,
                0.2,
                1
        );
        int hours = (int)Math.floor(timeLeft / 3.6e6);
        int minutes = (int)Math.floor((timeLeft % 3.6e6) / 6e4);
        int seconds = (int)Math.floor((timeLeft % 6e4) / 1000);

        String strHours = hours >= 10 ? String.valueOf(hours) : "0" + hours;
        String strMinutes = minutes >= 10 ? String.valueOf(minutes) : "0" + minutes;
        String strSeconds = seconds >= 10 ? String.valueOf(seconds) : "0" + seconds;

        String timeLeftTxt = strHours + ":" + strMinutes + ":" + strSeconds;

        txtTimer = view.findViewById(R.id.txt_timer);

        //setup function
        updateClothes(userManager);
        updateRoom(userManager);
        updateRadExp(userManager);


        //OBSERVER
        MessageQueue.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                MessageQueue.Message msg = MessageQueue.getInstance().peekMessage();

                if(msg.first == MessageQueue.MESSAGE_TYPE.RECIEVE_BLUETOOTH){
                    msg.handle();
                    BluetoothProtocolParser parser = new BluetoothProtocolParser();
                    BluetoothProtocolParser.Statement statement = parser.parse(msg.second);

                    //ROOM
                    if(statement.eventKey == 2000){
                        updateRoom(userManager);
                    }
                    //CLOTHES
                    else if (statement.eventKey == 2001){
                        updateClothes(userManager);
                    }
                    else if (statement.eventKey == 5000) {
                        updateRadExp(userManager);
                    }
                }

            }
        });


        //Time clocked in
        APIClient.getInstance().getLatestEventByKey(4010, userManager.getUser().getId(), new APICallback<Event>() {
            @Override
            public void onSuccess(Call<Event> call, Response<Event> response, Event decodedBody) {
                // TOOD uppdatera clocked in text
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("479"));
                ((TextView)view.findViewById(R.id.txt_clocked_in_time)).setText(
                        simpleDateFormat.format(decodedBody.getDateCreated())
                );
            }

            @Override
            public void onError(Call<Event> call, Response<Event> response, APIError error) {

            }
        });

        return view;
    }

    final Runnable updateTimerTextRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                double timeLeft = m_service.getTimeLeft();

                String timeLeftTxt = m_service.getHourString(timeLeft) + ":" +
                        m_service.getMinuteString(timeLeft) + ":" +
                        m_service.getSecondString(timeLeft);
                txtTimer.setText(timeLeftTxt);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                timerTextHandler.postDelayed(updateTimerTextRunnable, 1000);
            }
        }
    };

    //Go to clocked out fragment
    private void clockOut(){
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockOut());
        fragmentManager.commit();

        if (!isServiceRunning(RadiationTimerService.class)){
            m_context.stopService(m_serviceIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)m_context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isServiceRunning?", false+"");
        return false;
    }

    private void updateClothes(UserManager userManager){
        final UserManager mgr = userManager;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                APIClient.getInstance().getLatestEventByKey(2001, mgr.getUser().getId(), new APICallback<Event>() {
                    @Override
                    public void onSuccess(Call<Event> call, Response<Event> response, final Event decodedBody) {
                        Activity main = getActivity();

                        if(main == null)
                            return;

                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String clothes = decodedBody.getData();
                                    clothes = clothes.replaceAll("\n", "");
                                    clothes = clothes.replaceAll(" ", "");
                                    clothes = (clothes.equals("1") ? "Hazmat suit" : "Normal clothes");
                                    ((TextView)getView().findViewById(R.id.txt_clothing_text)).setText(clothes);
                                } catch(Exception e) {
                                    Log.e("UpdateClothes", e.getMessage());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Call<Event> call, Response<Event> response, APIError error) {
                        ((TextView)getView().findViewById(R.id.txt_clothing_text)).setText("Could not retrieve clothing status");
                    }
                });
            }
        }, DATABASE_DELAY);
    }

    private void updateRoom(UserManager userManager){
        final UserManager mgr = userManager;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                APIClient.getInstance().getLatestEventByKey(2000, mgr.getUser().getId(), new APICallback<Event>() {
                    @Override
                    public void onSuccess(Call<Event> call, Response<Event> response, final Event decodedBody) {
                        Activity main = getActivity();

                        if(main == null)
                            return;

                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String room = decodedBody.getData();

                                    room = room.replaceAll("\n", "");

                                    ((TextView)getView().findViewById(R.id.txt_room_text)).setText(room);
                                } catch(Exception e) {
                                    Log.e("UpdateRoom", e.getMessage());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Call<Event> call, Response<Event> response, APIError error) {
                        ((TextView)getView().findViewById(R.id.txt_clothing_text)).setText("Could not retrieve room status");
                    }
                });
            }
        }, DATABASE_DELAY);
    }

    //Update radiation
    public void updateRadExp(UserManager userManager){

        final UserManager mgr = userManager;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                APIClient.getInstance().getLatestEventByKey(5000, mgr.getUser().getId(), new APICallback<Event>() {
                    @Override
                    public void onSuccess(Call<Event> call, Response<Event> response, final Event decodedBody) {
                        Activity main = getActivity();

                        if(main == null)
                            return;

                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String rad = decodedBody.getData();

                                try {
                                    rad = rad.replaceAll("\n", "");
                                    rad = rad.replaceAll(" ", "");

                                    double asDouble = Double.parseDouble(rad);
                                    m_service.setRads(asDouble);
                                } catch(Exception e) {
                                    Log.e("UpdateRad",e.getMessage());
                                }

                            }
                        });
                    }

                    @Override
                    public void onError(Call<Event> call, Response<Event> response, APIError error) {

                    }
                });
            }
        }, DATABASE_DELAY);


    }

}
