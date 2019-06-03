package giltwizy.hopedevelopers.com.iotwheelchair;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //Initialization of the PermissionRequest code
    private final int bluetooth_permission_code = 1;
    private final int recordAudio_permission_code = 2;



    private TextView resultTv,statusValueTv;
    private Button connectBtn,disconnectBtn;
    private ImageView mic;
    private static final int voiceInputCode = 11;
    private String recognizerResult;

    // MAC address of remote Bluetooth device
    //private String address = "00:06:66:66:33:89";
    private String address = null;
    private String name=null;

    Set<BluetoothDevice> pairedDevices;
    //BluetoothAdapter bluetoothAdapter = null;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket bluetoothSocket=null;
    static final UUID uuid= UUID.randomUUID();

    OutputStream outputStream;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       check_bluetooth_permission();
       check_recordAudio_permission();

        connectBtn = (Button)findViewById(R.id.connect);
        disconnectBtn = (Button) findViewById(R.id.disconnect);
        statusValueTv = (TextView) findViewById(R.id.statusText);
        resultTv = (TextView) findViewById(R.id.resultTv);
        mic = (ImageView)findViewById(R.id.micImage);

    }


    public void check_bluetooth_permission(){
        //Check if Bluetooth permission is granted if not permission is requested
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            request_bluetooth_permission();
        }
    }

    public void check_recordAudio_permission(){
        //Check if Bluetooth permission is granted if not permission is requested
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            request_recordAudio_permission();
        }
    }

    //method requesting Bluetooth permission
    public void request_bluetooth_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.BLUETOOTH)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed so that this App can work to access the Bluetooth")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.BLUETOOTH}, bluetooth_permission_code);
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
                    new String[] {Manifest.permission.BLUETOOTH}, bluetooth_permission_code);
        }
    }


    //method requesting Record Audio permission
    private void request_recordAudio_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed so that this App can work to access the Bluetooth")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
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
        if ((requestCode == bluetooth_permission_code) || (requestCode == recordAudio_permission_code)){
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
                    recognizerResult = result.get(0);
                    //sendToBluetooth(recognizerResult);

                }

            }


    }

    private void sendToBluetooth(String recognizerResult) {
        try {
            if (bluetoothSocket!=null){
                bluetoothSocket.getOutputStream().write(recognizerResult.toString().getBytes());
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


    public void pairBluetooth() throws IOException {

        final int bluetoothInteger = 3;

        if(bluetoothAdapter==null)
        {
            Toast.makeText(this, "Your device doesn't support Bluetooth service", Toast.LENGTH_SHORT).show();
        }
        else {
            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, bluetoothInteger);
                try{
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    address = bluetoothAdapter.getAddress();
                    pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size()>0){
                        for (BluetoothDevice bluetoothDevice : pairedDevices){
                            address = bluetoothDevice.getAddress().toString();
                            name = bluetoothDevice.getName().toString();
                            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                catch (Exception we){

                }
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //get the mobile bluetooth device
                BluetoothDevice trojan = bluetoothAdapter.getRemoteDevice(address); //connect to the device
                bluetoothSocket = trojan.createInsecureRfcommSocketToServiceRecord(uuid); //creata RFCOM channel
                bluetoothSocket.connect();


            }
        }






    }







    public void disconnectBluetooth(View view) {
        bluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(),"Bluetooth Turned OFF", Toast.LENGTH_SHORT).show();
    }
}
