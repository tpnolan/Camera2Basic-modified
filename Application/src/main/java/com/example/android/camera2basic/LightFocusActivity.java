package com.example.android.camera2basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class LightFocusActivity extends AppCompatActivity {

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
    }

    private void displaySavedOrientations() {
        GameRotationVectorSensor sensor = GameRotationVectorSensor.getInstance(this);

        float[] orientations = sensor.getSavedOrientations();

        TextView savedAzimuthTextView = findViewById(R.id.savedAzimuth);
        savedAzimuthTextView.setText( ""+orientations[0] );

        TextView savedPitchTextView = findViewById(R.id.savedPitch);
        savedPitchTextView.setText( ""+orientations[1] );

        TextView savedRollTextView = findViewById(R.id.savedRoll);
        savedRollTextView.setText( ""+orientations[2] );
    }
}
