package se.ju.students.axam1798.andromeda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import se.ju.students.axam1798.andromeda.models.User;

public class EmployeeDetailFragment extends Fragment {

    public static final String EMPLOYEE_KEY = "EMPLOYEE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate) {

        View view = inflater.inflate(R.layout.fragment_employee, container, false);

        String employeeJson = getArguments().getString(EMPLOYEE_KEY, "");
        User employee = new Gson().fromJson(employeeJson, User.class);
        TextView textView = view.findViewById(R.id.test_text);
        textView.setText(employee.getRFID());

        return view;
    }
}
