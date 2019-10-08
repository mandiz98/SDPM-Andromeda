package se.ju.students.axam1798.andromeda.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import se.ju.students.axam1798.andromeda.R;


public class ClockOut extends Fragment {

    private Button mOkBtn;
    final Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clock_out, container, false);

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
            }, 3000);

        mOkBtn = view.findViewById(R.id.ok_btn);

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okClicked();
            }
        });
        return view;
    }
    //Remove fragment and return to main activity
    private void okClicked() {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(this);
            transaction.commit();
            fragmentManager.popBackStack();

    }
}
