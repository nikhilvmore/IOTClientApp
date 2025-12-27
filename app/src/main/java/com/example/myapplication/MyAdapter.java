package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<MyData> dataList;

    public MyAdapter(List<MyData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyData data = dataList.get(position);
        holder.column1.setText(data.getCol1());
        holder.column2.setText(data.getCol2());
        holder.column3.setText(data.getCol3());
        holder.column4.setText(data.getCol4());
        holder.column5.setText(data.getCol5());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

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