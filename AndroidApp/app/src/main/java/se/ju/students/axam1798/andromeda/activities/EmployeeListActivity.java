package se.ju.students.axam1798.andromeda.activities;

import android.content.Intent;
import android.os.Bundle;
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
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APICallback;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.API.APIError;
import se.ju.students.axam1798.andromeda.R;
import se.ju.students.axam1798.andromeda.adapters.EmployeeListViewAdapter;
import se.ju.students.axam1798.andromeda.models.User;

public class EmployeeListActivity extends AppCompatActivity {

    private static final String EMPLOYEE_SAVED_INSTANCE = "EMPLOYEES";

    private List<User> employeesList;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        this.gson = new Gson();

        if(savedInstanceState != null) {
            String employeesJson = savedInstanceState.getString(EMPLOYEE_SAVED_INSTANCE, "[]");
            Type userListType = new TypeToken<List<User>>(){}.getType();
            employeesList = gson.fromJson(employeesJson, userListType);
            setupEmployeeList();
        }else{
            APIClient.getInstance().getUsers(new APICallback<List<User>>(this) {
                @Override
                public void onSuccess(Call<List<User>> call, Response<List<User>> response, List<User> decodedBody) {
                    employeesList = decodedBody;
                    setupEmployeeList();
                }

                @Override
                public void onError(Call<List<User>> call, Response<List<User>> response, APIError error) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Could not get list of employeesList..",
                            Toast.LENGTH_LONG
                    ).show();
                    finish();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMPLOYEE_SAVED_INSTANCE, gson.toJson(employeesList));
    }

    private void setupEmployeeList() {
        ListView employeeListView = findViewById(R.id.employeeList);
        EmployeeListViewAdapter adapter = new EmployeeListViewAdapter(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                employeesList.toArray(new User[0])
        );
        employeeListView.setAdapter(adapter);

        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        employeesList.get(position).getRFID(),
                        Toast.LENGTH_SHORT
                ).show();

                Bundle extras = new Bundle();
                extras.putString(EmployeeHistoryActivity.EMPLOYEE_KEY, gson.toJson(employeesList.get(position)));
                Intent employeeHistoryIntent = new Intent(getApplicationContext(), EmployeeHistoryActivity.class);
                employeeHistoryIntent.putExtras(extras);
                startActivity(employeeHistoryIntent);
            }
        });
    }
}