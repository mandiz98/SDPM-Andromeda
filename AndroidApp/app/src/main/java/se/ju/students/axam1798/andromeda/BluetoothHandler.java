package se.ju.students.axam1798.andromeda;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Set;

public class BluetoothHandler
{
    private Context m_appContext = null;

    public BluetoothHandler(Context appContext)
    {
        m_appContext = appContext;
    }

    public boolean isSupported()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            return false;
        }

        return true;
    }

    public boolean isEnabled()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled())
        {
            return false;
        }

        return true;
    }

    public boolean connect(String name)
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        BluetoothDevice target = null;

        if(!bondedDevices.isEmpty())
        {
            for(BluetoothDevice device : bondedDevices)
            {
                if(device.getName().equals(name))
                {
                    target = device;

                    Log.i("BLUETOOTH_DEVICE", device.getName());
                }
            }
        }

        if(target != null)
        {
            BluetoothConnection connection = new BluetoothConnection(target);
            connection.run();
        }
        else
        {
            // Failed to find requested device
            return false;
        }

        return true;
    }
}
