package com.example.myapplication.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.FeedReaderDbHelper;
import com.example.myapplication.MyAdapter;
import com.example.myapplication.MyData;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<MyData> dataList;
    private FeedReaderDbHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the database helper
        dbHelper = new FeedReaderDbHelper(getContext());

        // Load data from the database instead of creating sample data
        dataList = dbHelper.getAllEntries();

        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // It's good practice to close the helper when the view is destroyed
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
