package com.example.android.camera2basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class LightFocusActivity extends AppCompatActivity {
    private Timer activityTimer = null;
    private static final String TAG = "LightFocus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_focus);
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar myToolbar = findViewById(R.id.focus_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        displaySavedOrientations();
        GameRotationVectorSensor sensor = GameRotationVectorSensor.getInstance(this);
        sensor.registerSensor();
        startLiveOrientations();
    }

    @Override
    public void onPause() {
        stopLiveOrientations();
        super.onPause();
        GameRotationVectorSensor sensor = GameRotationVectorSensor.getInstance(this);
        sensor.unregisterSensor();
    }

    private void startLiveOrientations() {
        this.activityTimer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                // whatever you need to do every 1/10 seconds
                displayLiveOrientations();
            }
        };

        activityTimer.schedule(myTask, 0, 100);
    }

    private void stopLiveOrientations() {
        activityTimer.cancel();
    }

    private void displayLiveOrientations() {
        GameRotationVectorSensor sensor = GameRotationVectorSensor.getInstance(this);

        final float[] orientations = sensor.getLiveOrientations();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                Log.e(TAG, "running on UI thread");

                // Stuff that updates the UI
                TextView liveAzimuthTextView = findViewById(R.id.liveAzimuth);
                liveAzimuthTextView.setText( String.format("%.4f", orientations[0]) );

                TextView livePitchTextView = findViewById(R.id.livePitch);
                livePitchTextView.setText( String.format("%.4f", orientations[1]) );

                TextView liveRollTextView = findViewById(R.id.liveRoll);
                liveRollTextView.setText( String.format("%.4f", orientations[2]) );
            }
        });
    }

    private void displaySavedOrientations() {
        GameRotationVectorSensor sensor = GameRotationVectorSensor.getInstance(this);

        float[] orientations = sensor.getSavedOrientations();

        TextView savedAzimuthTextView = findViewById(R.id.savedAzimuth);
        savedAzimuthTextView.setText( String.format("%.4f", orientations[0]) );

        TextView savedPitchTextView = findViewById(R.id.savedPitch);
        savedPitchTextView.setText( String.format("%.4f", orientations[1]) );

        TextView savedRollTextView = findViewById(R.id.savedRoll);
        savedRollTextView.setText( String.format("%.4f", orientations[2]) );
    }
}
