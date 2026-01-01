package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R; // This import is missing but required
import com.example.myapplication.MyData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat; // For formatting timestamp
import java.util.Date;             // For formatting timestamp
import java.util.List;
import java.util.Locale;           // For formatting timestamp

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<MyData> dataList;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public MyAdapter(List<MyData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the same list_item.xml layout as before
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyData data = dataList.get(position);

        // Bind the data from the MyData object to the TextViews
        // We use String.valueOf() to convert numbers to strings
        holder.column1.setText(data.getMachineName());
        holder.column2.setText(String.valueOf(data.getTemperature()));
        holder.column3.setText(String.valueOf(data.getSpeed()));
        holder.column4.setText(String.valueOf(data.getElectricityConsumption()));

        // Format the Unix timestamp (long) into a human-readable date string
        String formattedDate = sdf.format(new Date(data.getTimestamp() * 1000L));
        holder.column5.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class remains the same
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView column1, column2, column3, column4, column5;

        public MyViewHolder(View view) {
            super(view);
            column1 = view.findViewById(R.id.column1);
            column2 = view.findViewById(R.id.column2);
            column3 = view.findViewById(R.id.column3);
            column4 = view.findViewById(R.id.column4);
            column5 = view.findViewById(R.id.column5);
        }
    }
}
