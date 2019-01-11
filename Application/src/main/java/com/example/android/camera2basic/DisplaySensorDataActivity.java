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

    protected Sensor              gyroscopeSensor = null;
    protected SensorEventListener gyroscopeSensorListener = null;

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
        registerGyroscopeSensor();
    }

    private void unregisterSensors() {
        unregisterProximitySensor();
        unregisterGyroscopeSensor();
    }

    private void registerProximitySensor() {
        SensorManager sensorManager = getSensorManager();
        final Sensor proximitySensor = getProximitySensor(sensorManager);

        String message = getDisplayData();

        if(proximitySensor == null) {
            message += "Proximity sensor not available.\n";
            Log.e(TAG, message);
        } else {
            message += "Proximity sensor is available.\n" + proximitySensor.toString() + "\n";
            Log.e(TAG, message);
        }

        showData(message);

        // Create listener
        this.proximitySensorListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                String msg = getDisplayData();
                if(sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    // Detected something nearby
                    msg = getDisplayData() + String.format("Proximity event: %f \n", sensorEvent.values[0]);
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                } else {
                    // Nothing is nearby
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
                showData(msg);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                String msg = getDisplayData();
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

    private void registerGyroscopeSensor() {
        SensorManager sensorManager = getSensorManager();
        final Sensor gyroscopeSensor = getGyroscopeSensor(sensorManager);

        String message = getDisplayData();

        if(gyroscopeSensor == null) {
            message += "Gyroscope sensor not available.\n";
            Log.e(TAG, message);
        } else {
            message += "Gyroscope sensor is available.\n" + gyroscopeSensor.toString() + "\n";
            Log.e(TAG, message);
        }

        showData(message);

        // Create listener
        this.gyroscopeSensorListener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                String msg = getDisplayData();
                msg += "Gyroscope sensor accuracy changed to:" + i + "\n";
                showData(msg);
            }

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                handleGyroscopeEvent(sensorEvent);
            }

            private void handleGyroscopeEvent(SensorEvent sensorEvent) {
                String msg = getDisplayData();
                // values[0] == X axis
                if(sensorEvent.values[0] > 0.5f ) {
                    // Detected anti-clockwise around X axis
                    msg += "Gyroscope X-axis rotation: "+ sensorEvent.values[0] + "\n";
                    getWindow().getDecorView().setBackgroundColor(Color.GRAY);
                } else if(sensorEvent.values[0] < -0.5f ) {
                    // Detected clockwise around X axis
                    msg += "Gyroscope X-axis rotation: "+ sensorEvent.values[0] + "\n";
                    getWindow().getDecorView().setBackgroundColor(Color.MAGENTA);
                }

                // values[1] == Y axis
                if(sensorEvent.values[1] > 0.5f ) {
                    // Detected anti-clockwise around Y axis
                    msg += "Gyroscope Y-axis rotation: "+ sensorEvent.values[1] + "\n";
                    getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);
                } else if(sensorEvent.values[1] < -0.5f ) {
                    // Detected clockwise around Y axis
                    msg += "Gyroscope Y-axis rotation: "+ sensorEvent.values[1] + "\n";
                    getWindow().getDecorView().setBackgroundColor(Color.CYAN);
                }

                // values[2] == Z axis
                if(sensorEvent.values[2] > 0.5f ) {
                    // Detected anti-clockwise around Z axis
                    msg += "Gyroscope Z-axis rotation: "+ sensorEvent.values[2] + "\n";
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if(sensorEvent.values[2] < -0.5f ) {
                    // Detected clockwise around Z axis
                    msg += "Gyroscope Z-axis rotation: "+ sensorEvent.values[2] + "\n";
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
                showData(msg);
            }
        };

        // Register it, specifying the normal delay
        sensorManager.registerListener(this.gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterGyroscopeSensor() {
        SensorManager sensorManager = getSensorManager();
        Sensor gyroscopeSensor = getGyroscopeSensor(sensorManager);
        sensorManager.unregisterListener(this.gyroscopeSensorListener);
    }

    private Sensor getGyroscopeSensor(SensorManager sensorManager) {
        if( this.gyroscopeSensor == null ) {
            return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        } else {
            return this.gyroscopeSensor;
        }
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

    protected String getDisplayData() {
        TextView textView = findViewById(R.id.textView);
        return textView.getText().toString();
    }

    private void setUpToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
