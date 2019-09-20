package se.ju.students.axam1798.andromeda;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import se.ju.students.axam1798.andromeda.models.User;


public class MainActivity extends AppCompatActivity {

    private Button m_TestBtBtn;
    private User m_User;
    Intent m_ServiceIntent;
    private RadiationTimerService m_RadiationTimerService;
    Context ctx;

    public Context getCtx(){
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        m_RadiationTimerService = new RadiationTimerService(getCtx());
        m_ServiceIntent = new Intent(getCtx(),m_RadiationTimerService.getClass());
        if (!isMyServiceRunning(m_RadiationTimerService.getClass())){
            startService(m_ServiceIntent);
        }


        m_TestBtBtn= findViewById(R.id.test_bt_btn);
        m_TestBtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockIn();
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    //Go to clocked in fragment
    private void clockIn() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockedIn());

        m_User = new User(1,"hallo banan",true,false);
    }

    @Override
    protected void onDestroy() {
        stopService(m_ServiceIntent);
        Log.i("MAINACT","onDestroy!");
        super.onDestroy();
    }

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
