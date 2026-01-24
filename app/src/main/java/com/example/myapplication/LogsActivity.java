package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        TextView logTextView = findViewById(R.id.logTextView);

        try {
            // "logcat -d" dumps the current log buffer and exits
            // Use "logcat -v time" to include timestamps
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Optional: Filter for your specific app tag (MqttService)
                if (line.contains("MqttService") || line.contains("Netty")) {
                    log.append(line).append("\n");
                }
            }
            logTextView.setText(log.toString());
        } catch (IOException e) {
            logTextView.setText("Error loading logs: " + e.getMessage());
        }
    }
}