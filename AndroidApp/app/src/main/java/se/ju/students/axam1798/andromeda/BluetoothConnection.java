package se.ju.students.axam1798.andromeda;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnection extends Thread
{
    private final BluetoothSocket m_socket;
    private final BluetoothDevice m_device;

    private final String TAG = "BluetoothConnection";

    // m_uuid is the app's UUID string, also used in the server code.
    private final UUID m_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothConnection(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        m_device = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            tmp = device.createRfcommSocketToServiceRecord(m_uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        m_socket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            m_socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                m_socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }

            Log.e(TAG, "Could not connect to socket.");
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.

        // TODO
        // https://developer.android.com/guide/topics/connectivity/bluetooth#ManageAConnection
        //manageMyConnectedSocket(m_socket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            m_socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
