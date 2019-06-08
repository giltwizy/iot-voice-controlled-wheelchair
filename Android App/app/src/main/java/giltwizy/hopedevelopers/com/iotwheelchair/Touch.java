package giltwizy.hopedevelopers.com.iotwheelchair;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class Touch extends AppCompatActivity{

    public final String TAG = "Main";

    private Bluetooth bt;
    private TextView status;
//    private ImageButton up,left,stop,right,down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);

        status = (TextView) findViewById(R.id.textStatus);
//        up = (ImageButton) findViewById(R.id.upDirection);
//        left = (ImageButton) findViewById(R.id.leftDirection);
//        stop = (ImageButton) findViewById(R.id.stop);
//        right = (ImageButton) findViewById(R.id.rightDirection);
//        down = (ImageButton) findViewById(R.id.downDirection);

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectService();
            }
        });

        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectBluetooth();
            }
        });

        bt = new Bluetooth(this, mHandler);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    public void connectService() {
        try {
            status.setText(R.string.connecting);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-05");
                Log.d(TAG, "Btservice started - listening");
                status.setText(R.string.connected);
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                status.setText(R.string.notEnabled);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to start bt ", e);
            status.setText(R.string.unableToConnect);
        }
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME " + msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST " + msg);
                    break;
            }
        }
    };

    public void disconnectBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Bluetooth Turned OFF", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void goForward(View view) {
        String direction = "forward";
                bt.sendMessage(direction);
    }

    public void goLeft(View view) {
        String direction="left";
                bt.sendMessage(direction);
    }

    public void stop(View view) {
        String direction="stop";
                bt.sendMessage(direction);
    }

    public void goRight(View view) {
        String direction="right";
        bt.sendMessage(direction);
    }

    public void goBack(View view) {
        String direction="back";
        bt.sendMessage(direction);
    }
}
