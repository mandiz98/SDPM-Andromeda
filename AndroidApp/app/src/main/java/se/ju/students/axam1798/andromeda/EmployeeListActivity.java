package se.ju.students.axam1798.andromeda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.models.User;

public class EmployeeListActivity extends AppCompatActivity {

    private static final String EMPLOYEE_SAVED_INSTANCE = "EMPLOYEES";

    private List<User> employees;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        this.gson = new Gson();

        if(savedInstanceState != null) {
            String employeesJson = savedInstanceState.getString(EMPLOYEE_SAVED_INSTANCE, "[]");
            Type userListType = new TypeToken<List<User>>(){}.getType();
            employees = gson.fromJson(employeesJson, userListType);
            setupEmployeeList();
        }else{
            //APIClient.getInstance().getUsers();
        }
    }

    private void setupEmployeeList() {
        ListView employeeListView = findViewById(R.id.employeeList);
        // TODO: Ändra till en custom adapter
        ArrayAdapter<User> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                employees
        );
        employeeListView.setAdapter(adapter);

        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        employees.get(position).getRFID(),
                        Toast.LENGTH_SHORT
                ).show();

                Bundle bundle = new Bundle();
                bundle.putString(EmployeeDetailFragment.EMPLOYEE_KEY, gson.toJson(employees.get(position)));
                Fragment fragment = new EmployeeDetailFragment();
                fragment.setArguments(bundle);

                FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.fragment_container_employee, fragment);
                fragmentManager.addToBackStack(null);
                fragmentManager.commit();
            }
        });
    }
}