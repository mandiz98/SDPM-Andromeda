package se.ju.students.axam1798.andromeda;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class BluetoothConnection extends Thread
{
    static private BluetoothProtocolParser m_parser = new BluetoothProtocolParser();

    private Handler m_bluetoothInterface = new Handler() {
        public void handleMessage(android.os.Message msg)
        {
            if(msg.what == BluetoothConnection.MessageConstants.MESSAGE_READ)
            {
                String readMessage = null;
                try
                {
                    readMessage = (String)msg.obj;

                    BluetoothProtocolParser.Statement statement = m_parser.parse(readMessage);
                    if(statement.isComplete)
                    {
                        Log.i(TAG, new String("EVENTKEY:").concat(Integer.toString(statement.eventKey)));
                        Log.i(TAG, new String("TIMESTAMP:").concat(Long.toString(statement.timestamp)));
                        Log.i(TAG, new String("DATA:").concat(statement.data));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    public Handler getHandler()
    {
        return m_bluetoothInterface;
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    public interface MessageConstants
    {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket m_socket;
        private final InputStream m_inStream;
        private final OutputStream m_outStream;
        private byte[] m_buffer; // m_buffer store for the stream

        public ConnectedThread(BluetoothSocket socket)
        {
            m_socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try
            {
                tmpIn = socket.getInputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try
            {
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            m_inStream = tmpIn;
            m_outStream = tmpOut;
        }

        public void run()
        {
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
                            MessageConstants.MESSAGE_READ, numBytes, -1,
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
                        MessageConstants.MESSAGE_WRITE, -1, -1, m_buffer);
                writtenMsg.sendToTarget();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        m_bluetoothInterface.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                m_bluetoothInterface.sendMessage(writeErrorMsg);
            }
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

    private final BluetoothSocket m_socket;
    private final BluetoothDevice m_device;

    private final String TAG = "BluetoothConnection";

    // m_uuid is the app's UUID string, also used in the server code.
    private final UUID m_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothConnection(BluetoothDevice device) throws IOException {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        m_device = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            tmp = device.createRfcommSocketToServiceRecord(m_uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);

            throw e;
        }
        m_socket = tmp;
    }

    public void run() {
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
            try
            {
                m_socket.close();
            }
            catch (IOException closeException)
            {
                Log.e(TAG, "Could not close the client socket", closeException);
            }

            Log.e(TAG, "Could not connect to socket.");
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.

        // https://developer.android.com/guide/topics/connectivity/bluetooth#ManageAConnection
        ConnectedThread connectedThread = new ConnectedThread(m_socket);
        connectedThread.start();

        BluetoothProtocolParser parser = new BluetoothProtocolParser();
        BluetoothProtocolParser.Statement statement = new BluetoothProtocolParser.Statement();

        statement.eventKey = 3001;
        Log.i(TAG, parser.parse(statement));
        connectedThread.write(parser.parse(statement).getBytes());

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        statement.eventKey = 3000;
        Log.i(TAG, parser.parse(statement));
        connectedThread.write(parser.parse(statement).getBytes());
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
