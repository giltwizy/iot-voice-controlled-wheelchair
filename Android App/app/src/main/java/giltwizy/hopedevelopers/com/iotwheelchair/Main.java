package giltwizy.hopedevelopers.com.iotwheelchair;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Main extends Activity {

    public final String TAG = "Main";

    private SeekBar elevation;
    private TextView result,status,resultTv;
    private Bluetooth bt;


    private final int recordAudio_permission_code = 2;
    private static final int voiceInputCode = 11;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.resultTv);
        status = (TextView) findViewById(R.id.textStatus);


        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectService();
            }
        });

//        elevation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.d("Seekbar","onStopTrackingTouch ");
//                int progress = seekBar.getProgress();
//                String p = String.valueOf(progress);
//                result.setText(p);
//                bt.sendMessage(p);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                Log.d("Seekbar","onStartTrackingTouch ");
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                //Log.d("Seekbar", "onProgressChanged " + progress);
//            }
//        });

        bt = new Bluetooth(this, mHandler);
        connectService();



        check_recordAudio_permission();




    }


    public void check_recordAudio_permission(){
        //Check if Bluetooth permission is granted if not permission is requested
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            request_recordAudio_permission();
        }
    }



    //method requesting Record Audio permission
    private void request_recordAudio_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed so that this App can record the voice command")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Main.this,
                                    new String[] {Manifest.permission.RECORD_AUDIO}, recordAudio_permission_code);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, recordAudio_permission_code);
        }
    }



    //Result of requesting the permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == recordAudio_permission_code){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }





    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //You can set here own local Language.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sw");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, Tell me your direction");
        try {
            startActivityForResult(intent, voiceInputCode);
        }
        catch (ActivityNotFoundException a) {

        }
    }




    //After getting the result of the string it is transfters the string command via bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == voiceInputCode) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                resultTv.setText(result.get(0));
                String recognizerResult = result.get(0);
                //sendToBluetooth(recognizerResult);
                bt.sendMessage(recognizerResult);
                Log.d("Transfer","String transferred "+recognizerResult);

            }

        }


    }





    public void connectService(){
        try {
            status.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-05");
                Log.d(TAG, "Btservice started - listening");
                status.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                status.setText("Bluetooth Not enabled");
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to start bt ",e);
            status.setText("Unable to connect " +e);
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
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
            }
        }
    };

}
