package com.example.android.camera2basic;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DisplaySensorDataActivity extends AppCompatActivity {

    private static final boolean ENABLE_GYROSCOPE_ONLY_SENSOR = false;
    private static final boolean ENABLE_ROTATION_VECTOR_SENSOR = false;
    private static final String TAG = "SensorDataActivity";

    protected SensorManager       sensorManager   = null;
    protected Sensor              proximitySensor = null;
    protected SensorEventListener proximitySensorListener = null;

    protected Sensor              gyroscopeSensor = null;
    protected SensorEventListener gyroscopeSensorListener = null;

    protected Sensor              rotationVectorSensor = null;
    protected SensorEventListener rotationVectorSensorListener = null;

    protected GameRotationVectorSensor gameRotationVectorSensor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);
        setUpToolbar();
        TextView textView = findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
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
        if( ENABLE_GYROSCOPE_ONLY_SENSOR ) {
            registerGyroscopeSensor();
        }
        if( ENABLE_ROTATION_VECTOR_SENSOR ) {
            registerRotationVectorSensor();
        }
        this.gameRotationVectorSensor = GameRotationVectorSensor.getInstance(this);
        this.gameRotationVectorSensor.registerSensor();
    }

    private void unregisterSensors() {
        unregisterProximitySensor();
        if( ENABLE_GYROSCOPE_ONLY_SENSOR ) {
            unregisterGyroscopeSensor();
        }
        if( ENABLE_ROTATION_VECTOR_SENSOR ) {
            unregisterRotationVectorSensor();
        }
        this.gameRotationVectorSensor = GameRotationVectorSensor.getInstance(this);
        this.gameRotationVectorSensor.unregisterSensor();
    }

    private void registerRotationVectorSensor() {
        SensorManager sensorManager = getSensorManager();
        final Sensor rotationVectorSensor = getRotationVectorSensor(sensorManager);

        String message = getDisplayData();

        if(rotationVectorSensor == null) {
            message += "Rotation Vector sensor not available.\n";
            Log.e(TAG, message);
        } else {
            message += "Rotation Vector sensor is available.\n" +
                        rotationVectorSensor.toString() + "\n";
            Log.e(TAG, message);
        }

        showData(message);

        // Create listener
        this.rotationVectorSensorListener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                String msg = getDisplayData();
                msg += "Rotation Vector sensor accuracy changed to:" + i + "\n";
                showData(msg);
            }

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                handleRotationVectorEvent(sensorEvent);
            }
        };

        // Register it, specifying the normal delay
        sensorManager.registerListener(this.rotationVectorSensorListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void handleRotationVectorEvent(SensorEvent sensorEvent) {

        float[] orientations = getOrientationsArray(sensorEvent);

        // only focus on Z-Axis orientations for now.
        if(orientations[2] > 45) {
            String msg = getDisplayData();
            // print orientations only when orientation is > +45 degrees
            msg += "RVector Right: " + orientations[2] + "\n";
            getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            showData(msg);
        } else if(orientations[2] < -45) {
            String msg = getDisplayData();
            // print orientations only when orientation is < -45 degrees
            msg += "RVector Left: " + orientations[2] + "\n";
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            showData(msg);
        } else if(orientations[2] < 45 && orientations[2] > -45 && Math.abs(orientations[2]) > 30) {
            String msg = getDisplayData();
            // print orientations only when phone moving to an upright orientation, i.e between 30
            // and 45 degrees OR -30 to -45 degrees.
            // Doing it this way to prevent too
            // much output when phone is almost upright.
            msg += "RVector upright: " + orientations[2] + "\n";
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            showData(msg);
        }
        // do not print anything from 30 to -30 degrees.
    }

    /**
     * get the orientations array (rotations against [X,Y,Z]) in degrees as an array of floats.
     *
     * The rotation vector sensor combines raw data generated by the gyroscope, accelerometer,
     * and magnetometer to create a quaternion. Consequently, the values array of its SensorEvent
     * object has the following five elements:
     *
     *  - The X, Y, Z, and W components of the quaternion
     *  - A heading accuracy
     *
     * You can convert the quaternion into a rotation matrix, a 4x4 matrix, by using the
     * getRotationMatrixFromVector() method of the SensorManager class.
     *
     * If you are developing an OpenGL app, you can use the rotation matrix directly to transform
     * objects in your 3D scene. For now, however, let us convert the rotation matrix into an
     * array of orientations, specifying the rotation of the device along the Z, X, and Y axes.
     * To do so, we can use the getOrientation() method of the SensorManager class.
     *
     * Before you call the getOrientation() method, you must remap the coordinate system of the
     * rotation matrix. More precisely, you must rotate the rotation matrix such that the Z-axis
     * of the new coordinate system coincides with the Y-axis of the original coordinate system.
     *
     * @param sensorEvent the given SensorEvent
     * @return [X,Y,Z] orientations array in degrees as an array of floats.
     */
    private float[] getOrientationsArray(SensorEvent sensorEvent) {
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector( rotationMatrix, sensorEvent.values);

        // Remap coordinate system
        float[] remappedRotationMatrix = new float[16];
        SensorManager.remapCoordinateSystem(rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix);

        // Convert to orientations
        float[] orientations = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientations);


        // Convert from Radians to Degrees
        for(int i = 0; i < 3; i++) {
            orientations[i] = (float)(Math.toDegrees(orientations[i]));
        }
        return orientations;
    }

    private void unregisterRotationVectorSensor() {
        SensorManager sensorManager = getSensorManager();
        sensorManager.unregisterListener(this.rotationVectorSensorListener);
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
                    msg = getDisplayData() + "Proximity event: " +
                            ReflectionToStringBuilder.toString(sensorEvent) + "\n";
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
        };

        // Register it, specifying the normal delay
        sensorManager.registerListener(this.gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterGyroscopeSensor() {
        SensorManager sensorManager = getSensorManager();
        sensorManager.unregisterListener(this.gyroscopeSensorListener);
    }

    private Sensor getGyroscopeSensor(SensorManager sensorManager) {
        if( this.gyroscopeSensor == null ) {
            this.gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
        return this.gyroscopeSensor;
    }

    private Sensor getProximitySensor(SensorManager sensorManager) {
        if( this.proximitySensor == null ) {
            this.proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        return this.proximitySensor;
    }

    private SensorManager getSensorManager() {
        if( this.sensorManager == null ) {
            this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }
        return this.sensorManager;
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

    protected void handleGyroscopeEvent(SensorEvent sensorEvent) {
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

    private Sensor getRotationVectorSensor(SensorManager sensorManager) {
        if( this.rotationVectorSensor == null ) {
            this.rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        return this.rotationVectorSensor;
    }
}
