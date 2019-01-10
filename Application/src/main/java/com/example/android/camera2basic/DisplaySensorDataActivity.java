package com.example.android.camera2basic;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

public class DisplaySensorDataActivity extends AppCompatActivity {

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "DisplaySensorDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SensorManager sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor proximitySensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        String message = "Sensor data here...\n";

        if(proximitySensor == null) {
            message = "Proximity sensor not available.";
            Log.e(TAG, message);
        } else {
            message += "Proximity sensor is available.\n";
            message += proximitySensor.toString();
            Log.e(TAG, message);
        }

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}
