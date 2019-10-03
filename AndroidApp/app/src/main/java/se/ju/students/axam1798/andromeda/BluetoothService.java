package se.ju.students.axam1798.andromeda;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import se.ju.students.axam1798.andromeda.exceptions.NotPairedException;

public class BluetoothService {

    private final String TAG = "BLUETOOTH";

    public BluetoothService()
    {

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

    public BluetoothDevice getPairedDevice(String name)
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(!bondedDevices.isEmpty())
        {
            for(BluetoothDevice device : bondedDevices)
            {
                if(device.getName().equals(name))
                {
                    return device;
                }
            }
        }

        return null;
    }

    public BluetoothConnection connect(BluetoothDevice device, Handler handler) throws NotPairedException {
        return new BluetoothConnection(device, handler);
    }

    public class BluetoothConnection extends Thread
    {
        // Defines several constants used when transmitting messages between the
        // service and the UI.
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        private Handler m_bluetoothInterface = null;

        private final BluetoothDevice m_device;
        private final BluetoothSocket m_socket;
        private final InputStream m_inStream;
        private final OutputStream m_outStream;
        private byte[] m_buffer; // m_buffer store for the stream

        private final UUID m_uuid; //UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public BluetoothConnection(BluetoothDevice device, Handler handler) throws NotPairedException {
            this.setHandler(handler);

            // Setup socket
            BluetoothSocket tmp = null;
            m_device = device;
            try {
                m_uuid = m_device.getUuids()[0].getUuid();
            }catch(NullPointerException ex) {
                throw new NotPairedException();
            }

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                tmp = device.createRfcommSocketToServiceRecord(m_uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            m_socket = tmp;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try
            {
                tmpIn = m_socket.getInputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when creating input stream.", e);
            }
            try
            {
                tmpOut = m_socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream.", e);
            }

            m_inStream = tmpIn;
            m_outStream = tmpOut;
        }

        public void run()
        {
            // start connection

            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();

            try
            {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                m_socket.connect();
            }
            catch (IOException connectException)
            {
                // Unable to connect; close the socket and return.
                try {
                    m_socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }

                Log.e(TAG, "Could not connect to socket.");
                return;
            }

            // enter data loop
            m_buffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true)
            {
                try
                {
                    // Read from the InputStream.
                    numBytes = m_inStream.read(m_buffer);
                    String message = new String(m_buffer, 0, numBytes, "ASCII");

                    // Send the obtained bytes to the UI activity.
                    Message readMsg = m_bluetoothInterface.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            message);
                    readMsg.sendToTarget();
                }
                catch (IOException e)
                {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes)
        {
            try
            {
                m_outStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = m_bluetoothInterface.obtainMessage(
                        MESSAGE_WRITE, -1, -1, m_buffer);
                writtenMsg.sendToTarget();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        m_bluetoothInterface.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                m_bluetoothInterface.sendMessage(writeErrorMsg);
            }
        }

        public void setHandler(Handler communicationChannel)
        {
            m_bluetoothInterface = communicationChannel;
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel()
        {
            try
            {
                m_socket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
