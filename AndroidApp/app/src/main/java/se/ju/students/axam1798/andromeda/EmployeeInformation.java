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

public class EmployeeInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_information);

        final String[] values = new String[] {"Emp 1", "Emp 2", "Emp 3"};

        ListView listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(EmployeeInformation.this, values[position], Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("123", values[position]);
                Fragment fragment = new EmployeeFrag();
                fragment.setArguments(bundle);

                FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.fragment_container_employee, fragment);
                fragmentManager.addToBackStack(null);
                fragmentManager.commit();

            }
        });
        }
}