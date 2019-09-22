package se.ju.students.axam1798.andromeda;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothService m_bluetoothService = null;
    private BluetoothProtocolParser m_parser = new BluetoothProtocolParser();
    private BluetoothService.BluetoothConnection m_connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_bluetoothService = new BluetoothService();
        if(m_bluetoothService.isSupported())
        {
            if(!m_bluetoothService.isEnabled())
            {
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);
            }
            else
            {
                setupBTConnection();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setupBTConnection();
    }

    public void setupBTConnection()
    {
        m_connection = m_bluetoothService.connect(
            m_bluetoothService.getPairedDevice("HC-06"),
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if(msg.what == BluetoothService.BluetoothConnection.MESSAGE_READ)
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
            }
        );

        m_connection.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        m_connection.cancel();
    }
}
