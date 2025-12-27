package com.example.myapplication.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MyAdapter;
import com.example.myapplication.MyData;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<MyData> dataList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create some sample data
        dataList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            dataList.add(new MyData("Row " + i, "Data A", "Data B", "Data C", "Data D"));
        }

        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);

        return root;
    }
}
