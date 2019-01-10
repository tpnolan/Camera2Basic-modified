package com.example.android.camera2basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DisplaySensorDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);

        String message = "Sensor data here...";

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}
