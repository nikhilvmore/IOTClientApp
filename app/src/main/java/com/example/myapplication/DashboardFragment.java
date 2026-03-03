package com.example.myapplication;

import android.graphics.Color; // Changed from pdf.ink
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry; // Chart entry
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private LineChart chart;
    private TextView tvAvgTemp, tvStatus;
    private FeedReaderDbHelper dbHelper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        chart = view.findViewById(R.id.reportingChart);
        tvAvgTemp = view.findViewById(R.id.tvAvgTemp);
        tvStatus = view.findViewById(R.id.tvStatus);
        dbHelper = new FeedReaderDbHelper(getContext());

        setupChart();
        loadAnalysisData();
        return view;
    }

    private void setupChart() {
        if (chart == null) return;
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
    }

    // Inside loadAnalysisData()

    private void loadAnalysisData() {
        List<MyData> data = dbHelper.getAllMessages();
        if (data.isEmpty()) return;

        ArrayList<Entry> entries = new ArrayList<>();
        float totalTemp = 0;

        for (int i = 0; i < data.size(); i++) {
            float val = (float) data.get(i).getTemperature();
            entries.add(new Entry(i, val)); // Graph data point
            totalTemp += val;
        }

        // --- Analysis ---
        float avg = totalTemp / data.size();
        tvAvgTemp.setText(String.format("Average Temp: %.1f°C", avg));

        if (avg > 75) {
            tvStatus.setText("Warning: High Load Detected");
            tvStatus.setTextColor(Color.RED);
        } else {
            tvStatus.setText("System: Normal");
            tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        }

        LineDataSet set = new LineDataSet(entries, "Temperature Trend");
        set.setColor(Color.BLUE);
        set.setLineWidth(2f);
        chart.setData(new LineData(set));
        chart.invalidate(); // Refresh the graph
    }

    // ... existing code ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If you added a refresh button in XML, link it here
        View btnRefresh = view.findViewById(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> refreshData());
        }
    }

    /**
     * Call this method to update the graph and analysis dynamically
     * when new data arrives in the database.
     */
    public void refreshData() {
        if (dbHelper != null) {
            loadAnalysisData();
        }
    }

    public static String getMqttUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        // Defaulting to your specified IP
        return prefs.getString("mqtt_server_uri", "tcp://192.168.1.4:1883");
    }

    public static void setMqttUri(Context context, String newUri) {
        context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE)
                .edit()
                .putString("mqtt_server_uri", newUri)
                .apply();
    }
}