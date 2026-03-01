package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.databinding.ItemMessageBinding;
import com.example.myapplication.databinding.ListItemBinding;

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
        // Use ViewBinding to inflate the layout
//        ListItemBinding binding = ListItemBinding.inflate(
//                LayoutInflater.from(parent.getContext()), parent, false);
//        return new MyViewHolder(binding);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        MyData data = dataList.get(position);
//        holder.binding.tvMachine.setText(data.getMachineName());
//        holder.binding.tvTemp.setText(String.valueOf(data.getTemperature()));
//        holder.binding.tvSpeed.setText(String.valueOf(data.getSpeed()));
//        holder.binding.tvPower.setText(String.valueOf(data.getElectricity()));
//        holder.binding.tvTimestamp.setText(new java.util.Date(data.getTimestamp()).toString());
//    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyData data = dataList.get(position);

        // Bind the data from the MyData object to the TextViews
        // We use String.valueOf() to convert numbers to strings
        holder.tvMachine.setText(data.getMachineName());
        holder.tvTemp.setText(String.valueOf(data.getTemperature()));
        holder.tvSpeed.setText(String.valueOf(data.getSpeed()));
        holder.tvPower.setText(String.valueOf(data.getElectricityConsumption()));

        // Format the Unix timestamp (long) into a human-readable date string
        String formattedDate = sdf.format(new Date(data.getTimestamp() * 1000L));
        holder.tvTimestamp.setText(formattedDate);
    }

    public void updateData(List<MyData> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class remains the same
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView column1, column2, column3, column4, column5;
//
//        public MyViewHolder(View view) {
//            super(view);
//            column1 = view.findViewById(R.id.column1);
//            column2 = view.findViewById(R.id.column2);
//            column3 = view.findViewById(R.id.column3);
//            column4 = view.findViewById(R.id.column4);
//            column5 = view.findViewById(R.id.column5);
//        }
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // 1. Declare the variable
        TextView tvMachine;
        TextView tvTemp;
        TextView tvSpeed;
        TextView tvPower;
        TextView tvTimestamp;

        public MyViewHolder(View itemView) {
            super(itemView);

            // 2. LINK the variable to the XML ID
            // Check that R.id.your_text_id matches the ID in your XML file exactly!
            tvMachine = itemView.findViewById(R.id.tvMachine);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            tvPower = itemView.findViewById(R.id.tvPower);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
