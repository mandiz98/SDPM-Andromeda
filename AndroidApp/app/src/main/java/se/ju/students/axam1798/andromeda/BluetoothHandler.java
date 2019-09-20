package se.ju.students.axam1798.andromeda;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothHandler
{
    private Context m_appContext = null;
    private BluetoothConnection m_connection = null;

    private String TAG = "BluetoothHandler";

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

                    Log.i(TAG, device.getName());
                }
            }
        }

        if(target != null)
        {
            try
            {
                m_connection = new BluetoothConnection(target);

                m_connection.run();

                return true;
            }
            catch(Exception e)
            {
                Log.e(TAG, "Device paired but could not establish bluetooth connection.", e);

                return false;
            }
        }
        else
        {
            // Failed to find requested device
            return false;
        }
    }

    public Handler getHandler()
    {
        return m_connection.getHandler();
    }
}
