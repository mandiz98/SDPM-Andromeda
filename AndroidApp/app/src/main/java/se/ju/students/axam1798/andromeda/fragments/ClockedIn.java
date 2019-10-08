package se.ju.students.axam1798.andromeda.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import se.ju.students.axam1798.andromeda.R;
import se.ju.students.axam1798.andromeda.UserManager;
import se.ju.students.axam1798.andromeda.activities.EmployeeHistoryActivity;
import se.ju.students.axam1798.andromeda.activities.EmployeeListActivity;
import se.ju.students.axam1798.andromeda.enums.Role;


public class ClockedIn extends Fragment {

    //private Button mTestClockOutBtn ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_clocked_in, container, false);

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

        UserManager userManager = UserManager.getInstance(getContext());
        if(userManager.getUser().getRole() == Role.MANAGER) {
            Menu menu = toolbar.getMenu();
            menu.getItem(0).setVisible(false); // My history
            menu.getItem(1).setVisible(true); // Employees
        }

        return view;
    }

    //Go to clocked out fragment
    private void clockOut(){
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockOut());
        fragmentManager.commit();
    }

}
