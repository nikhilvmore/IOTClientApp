package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<MyData> messageList = new ArrayList<>();

    public void setData(List<MyData> newData) {
        this.messageList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyData data = messageList.get(position);

        // Bind DB fields to your XML Table Columns
        holder.binding.tvMachine.setText(data.getMachine());
        holder.binding.tvTemp.setText(data.getTemp() + "°C");
        holder.binding.tvSpeed.setText(data.getSpeed());
        holder.binding.tvPower.setText(data.getPower());
        holder.binding.tvTimestamp.setText(String.valueOf(data.getTimestamp()));
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    public void addMessage(String message) {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding binding;
        ViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}