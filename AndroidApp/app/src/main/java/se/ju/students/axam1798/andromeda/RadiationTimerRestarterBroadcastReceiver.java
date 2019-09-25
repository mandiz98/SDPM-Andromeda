package se.ju.students.axam1798.andromeda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RadiationTimerRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RadiationTimerRestarterBroadcastReceiver.class.getSimpleName(),"SERVICE STOPS! NOT GOOD!!!");
        context.startService(new Intent(context,RadiationTimerService.class));
    }
}
