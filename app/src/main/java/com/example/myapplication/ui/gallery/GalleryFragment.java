package com.example.myapplication.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        GalleryViewModel galleryViewModel =
//                new ViewModelProvider(this).get(GalleryViewModel.class);
//
//        binding = FragmentGalleryBinding.inflate(inflater, container, false);
//

        // --- 1. Initialize DB Helper ---
        com.example.myapplication.FeedReaderDbHelper dbHelper = new com.example.myapplication.FeedReaderDbHelper(getContext());

        // --- 2. Load Data for Analysis ---
        java.util.List<com.example.myapplication.MyData> dataList = dbHelper.getAllMessages();
        binding = FragmentGalleryBinding.inflate(inflater, container, false);

        if (dataList != null && !dataList.isEmpty()) {
            java.util.ArrayList<com.github.mikephil.charting.data.Entry> entries = new java.util.ArrayList<>();
            float totalTemp = 0;

            for (int i = 0; i < dataList.size(); i++) {
                float val = (float) dataList.get(i).getTemperature();
                entries.add(new com.github.mikephil.charting.data.Entry(i, val));
                totalTemp += val;
            }

            // --- 3. Update Analysis UI ---
            float avg = totalTemp / dataList.size();
            binding.tvAvgTemp.setText(String.format("Average Temp: %.2f°C", avg));

            if (avg > 75) {
                binding.tvStatus.setText("Status: Warning - High Temp");
                binding.tvStatus.setTextColor(android.graphics.Color.RED);
            } else {
                binding.tvStatus.setText("Status: Normal");
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
            }

            // --- 4. Setup and Show the Chart ---
            com.github.mikephil.charting.data.LineDataSet dataSet = new com.github.mikephil.charting.data.LineDataSet(entries, "Temperature History");
            dataSet.setColor(android.graphics.Color.BLUE);
            dataSet.setLineWidth(2f);
            dataSet.setCircleColor(android.graphics.Color.BLUE);
            dataSet.setDrawValues(false);

            com.github.mikephil.charting.data.LineData lineData = new com.github.mikephil.charting.data.LineData(dataSet);
            binding.reportingChart.setData(lineData);
            binding.reportingChart.invalidate(); // Refresh the view
            binding.reportingChart.getDescription().setEnabled(false);
        } else {
            // No data in DB yet
            binding.tvStatus.setText("Status: No Data Available");
        }

        View root = binding.getRoot();

       // final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}