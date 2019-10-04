package se.ju.students.axam1798.andromeda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EmployeeFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate) {

        View view = inflater.inflate(R.layout.fragment_employee, container, false);

        String string = getArguments().getString("123");
        TextView textView = (TextView)view.findViewById(R.id.test_text);
        textView.setText(string);

        return view;
    }
}
