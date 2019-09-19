package se.ju.students.axam1798.andromeda;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button mTestBtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestBtBtn = findViewById(R.id.test_bt_btn);
        mTestBtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockIn();
            }
        });
    }

    //Go to clocked in fragment
    private void clockIn() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_container, new ClockedIn());
        fragmentManager.commit();
    }

}
