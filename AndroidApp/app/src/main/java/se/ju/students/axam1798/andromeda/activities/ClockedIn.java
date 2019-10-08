package se.ju.students.axam1798.andromeda.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.ju.students.axam1798.andromeda.R;


public class ClockedIn extends Fragment {

    //private Button mTestClockOutBtn ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clocked_in, container, false);

        return view;
    }

    //Go to clocked out fragment
    private void clockOut(){
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockOut());
        fragmentManager.commit();
    }

}
