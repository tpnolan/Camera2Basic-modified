package com.example.android.camera2basic;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

public class DisplaySensorDataActivity extends AppCompatActivity {

    private static final String TAG = "SensorDataActivity";

    protected SensorManager       sensorManager   = null;
    protected Sensor              proximitySensor = null;
    protected SensorEventListener proximitySensorListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);

        setUpToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    public void onPause() {
        unregisterSensors();
        super.onPause();
    }

    private void registerSensors() {
        registerProximitySensor();
    }

    private void unregisterSensors() {
        unregisterProximitySensor();
    }

    private void registerProximitySensor() {
        SensorManager sensorManager = getSensorManager();
        final Sensor proximitySensor = getProximitySensor(sensorManager);

        final String message;

        if(proximitySensor == null) {
            message = "Proximity sensor not available.\n";
            Log.e(TAG, message);
        } else {
            message = "Proximity sensor is available.\n" + proximitySensor.toString() + "\n";
            Log.e(TAG, message);
        }

        showData(message);

        // Create listener
        this.proximitySensorListener = new SensorEventListener() {
            private String msg = message;

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                msg += "Proximity event generated:\n";
                msg += sensorEvent.values[0] + "\n";
                showData(msg);
                if(sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    // Detected something nearby
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                } else {
                    // Nothing is nearby
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                msg += "Proximity sensor accuracy changed to:" + i + "\n";
                showData(msg);
            }
        };

        // Register it, specifying the polling interval in microseconds
        sensorManager.registerListener(this.proximitySensorListener,
                proximitySensor, 2 * 1000 * 1000);
    }

    private void unregisterProximitySensor() {
        SensorManager sensorManager = getSensorManager();
        Sensor proximitySensor = getProximitySensor(sensorManager);
        sensorManager.unregisterListener(this.proximitySensorListener);
    }

    private Sensor getProximitySensor(SensorManager sensorManager) {
        if( this.proximitySensor == null ) {
            return sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        } else {
            return this.proximitySensor;
        }
    }

    private SensorManager getSensorManager() {
        if( this.sensorManager == null ) {
            return (SensorManager) getSystemService(SENSOR_SERVICE);
        } else {
            return this.sensorManager;
        }
    }

    protected void showData(String message) {
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }

    private void setUpToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
