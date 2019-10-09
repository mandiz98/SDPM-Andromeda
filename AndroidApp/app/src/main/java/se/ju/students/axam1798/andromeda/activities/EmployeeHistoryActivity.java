package se.ju.students.axam1798.andromeda.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APICallback;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.API.APIError;
import se.ju.students.axam1798.andromeda.R;
import se.ju.students.axam1798.andromeda.UserManager;
import se.ju.students.axam1798.andromeda.fragments.HistoryPageFragment;
import se.ju.students.axam1798.andromeda.models.Event;
import se.ju.students.axam1798.andromeda.models.User;

public class EmployeeHistoryActivity extends FragmentActivity {

    public static final String EMPLOYEE_KEY = "EMPLOYEE";

    private static final int NUM_PAGES = 2;

    private User employee;
    private Gson gson;

    // ViewPager stuffz
    private ViewPager m_pager;
    private PagerAdapter m_pagerAdapter;

    private HistoryPageFragment radiationHistoryFragment;
    private HistoryPageFragment workHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_history);

        gson = new Gson();

        // TODO: Get events from API, populate fragments

        radiationHistoryFragment = HistoryPageFragment.newInstance(
                "Radiation",
                Arrays.asList(
                        new Event(0, 0, 4000, new Date(), "467450"), // Radiation safety limit in "data" variable
                        new Event(0, 0, 4000, new Date(), "485023"),
                        new Event(0, 0, 4000, new Date(), "493829")
                ),
                android.R.layout.simple_list_item_1 // TODO: Use custom radiation history list layout
        );
        workHistoryFragment = HistoryPageFragment.newInstance(
                "Work",
                Arrays.asList(
                        new Event(0, 0, 4010, new Date(), "rfid")
                ),
                android.R.layout.simple_list_item_1 // TODO: Use custom work history list layout
        );

        APIClient.getInstance().getEventsByKey(
                4010,
                UserManager.getInstance(getApplicationContext()).getUser().getId(),
                new APICallback<List<Event>>() {
            @Override
            public void onSuccess(Call<List<Event>> call, Response<List<Event>> response, List<Event> decodedBody) {
                workHistoryFragment.setEvents(decodedBody);
            }

            @Override
            public void onError(Call<List<Event>> call, Response<List<Event>> response, APIError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        APIClient.getInstance().getEventsByKey(
                5000,
                UserManager.getInstance(getApplicationContext()).getUser().getId(),
                new APICallback<List<Event>>() {
            @Override
            public void onSuccess(Call<List<Event>> call, Response<List<Event>> response, List<Event> decodedBody) {
                radiationHistoryFragment.setEvents(decodedBody);
            }

            @Override
            public void onError(Call<List<Event>> call, Response<List<Event>> response, APIError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Instantiate a ViewPager and a PagerAdapter.
        m_pager = findViewById(R.id.historyPager);
        m_pagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager());
        m_pager.setAdapter(m_pagerAdapter);

        // Get the user from saved instance or from the intent's extras
        String employeeJson;
        if(savedInstanceState != null) {
            employeeJson = savedInstanceState.getString(EMPLOYEE_KEY);
        }else{
            employeeJson = this.getIntent().getStringExtra(EMPLOYEE_KEY);
        }
        employee = gson.fromJson(employeeJson, User.class);
    }

    @Override
    public void onBackPressed() {
        if (m_pager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            m_pager.setCurrentItem(m_pager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMPLOYEE_KEY, gson.toJson(employee));
    }

    private class HistoryPagerAdapter extends FragmentPagerAdapter {

        public HistoryPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: {
                    return radiationHistoryFragment;
                }
                case 1: {
                    return workHistoryFragment;
                }
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
