package se.ju.students.axam1798.andromeda.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;

import se.ju.students.axam1798.andromeda.R;


public class ClockOut extends Fragment {

    private Button mOkBtn;
    final Handler mHandler = new Handler();
    private View m_view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_view = inflater.inflate(R.layout.fragment_clock_out, container, false);
        //Change back to main activity after a delay
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    okClicked();
                } catch(NullPointerException e){
                    System.out.println("Caught the NullPointerException");
                }
            }
        }, 5000);

        mOkBtn = m_view.findViewById(R.id.ok_btn);

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okClicked();
            }
        });

        timeClockedOut();

        return m_view;
    }
    //Remove fragment and return to main activity
    private void okClicked() {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(this);
            transaction.commit();
            fragmentManager.popBackStack();

    }

    private void timeClockedOut() {
        String m_time;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        m_time = sdf.format(calendar.getTime());

        Activity main = getActivity();

        if(main == null)
            return;

        final String time = m_time;
        main.runOnUiThread(new Runnable() {
           @Override
            public void run() {
               ((TextView)m_view.findViewById(R.id.txt_clocked_out_at_timer)).setText(time);
            }
        });
    }
}
