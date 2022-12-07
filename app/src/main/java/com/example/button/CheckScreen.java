package com.example.button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CheckScreen extends AppCompatActivity implements SensorEventListener {
    private MaterialButton button,audio;
    private EditText wifi;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Intent home;
    private static final int SPEECH_REQUEST_CODE = 0;
    private String spokenText="";
    private Intent intent;
    private boolean isCharging;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_screen);
        home = new Intent(CheckScreen.this, SuccessScreen.class);
        findView();
        initLight();
        audio.setOnClickListener(view->initVoise());
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCharging=checkBattery();
                if(isCharging==false) {
                    wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo.getSSID().equalsIgnoreCase("\""+wifi.getText().toString()+"\"")) {
                            if (lightSensor.getType() == Sensor.TYPE_LIGHT && event.values[0] > 0.0) {
                                if (spokenText.equalsIgnoreCase("please")||spokenText.equalsIgnoreCase("בבקשה")) {
                                    startActivity(home);
                                    finish();
                                    //message.setText("success");
                                } else if (spokenText.equalsIgnoreCase("")) {
                                    //warning.setText("*Try to say it again");
                                    popUpMessege("Make sure you actually said the word", "mic");
                                } else if (!spokenText.equalsIgnoreCase("") && !spokenText.equalsIgnoreCase("please")) {
                                    popUpMessege("Try to say it again", "mic");
                                }
                            } else {
                                //warning.setText("*It's a little dark in here, don't you think? ");
                                popUpMessege("It's a little dark in here, don't you think? ", "light");
                            }

                    } else if(wifiInfo.getSSID().equalsIgnoreCase("<unknown ssid>")){
                        popUpMessege("Please write the name of your wifi connection", "wifi");
                    }else{
                        popUpMessege("Not the right name.. try again", "wifi");
                    }

                }else{
                    //warning.setText("*Pay attention that the phone is not in the charger ");
                    popUpMessege("Pay attention that the phone is not connected to a charger ","charger");

                }
            }
        });


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get spoken
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void popUpMessege(String message,String photo){
        Dialog dialog=new Dialog((this));
        dialog.setContentView(R.layout.layout_message);
        MaterialButton close=dialog.findViewById(R.id.close);
        TextView notice=dialog.findViewById(R.id.text_message);
        ImageView photoView=dialog.findViewById(R.id.photo);
        notice.setText(message);
        switch (photo){
            case "mic":
                photoView.setImageResource(R.drawable.microphone);
                break;
            case "wifi":
                photoView.setImageResource(R.drawable.wifi);
                break;
            case "light":
                photoView.setImageResource(R.drawable.light);
                break;
            case "charger":
                photoView.setImageResource(R.drawable.charger);
                break;

        }

        close.setOnClickListener(view->dialog.dismiss());
        dialog.show();

    }

    private boolean checkBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING ;
    }

    private void initVoise() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What is the magic word?");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void initLight() {
        sensorManager =(SensorManager)getSystemService(Service.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }



    private void findView() {
        button=findViewById(R.id.button);
        audio=findViewById(R.id.audio);
        wifi =findViewById(R.id.word);
    }
}