package com.example.myapplication.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myapplication.FeedReaderDbHelper;
import com.example.myapplication.MyAdapter;
import com.example.myapplication.MyData;
import com.example.myapplication.databinding.FragmentListBinding;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private FragmentListBinding binding;
    private MyAdapter adapter;
    private String topicName;
    private FeedReaderDbHelper dbHelper;

    public static ListFragment newInstance(String topic) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("topic_name", topic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicName = getArguments().getString("topic_name");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        dbHelper = new FeedReaderDbHelper(getContext());

        // Initialize RecyclerView with empty list
        adapter = new MyAdapter(new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        loadDatabaseData();
        return binding.getRoot();
    }

    private void loadDatabaseData() {
        // Query DB for messages specifically for this Machine/Topic
        String DEFAULT_TOPIC = "Machine A";
        List<MyData> dataList = dbHelper.getMessagesByTopic(DEFAULT_TOPIC);

        if (dataList.isEmpty()) {
            // This confirms the DB query returned nothing
            Toast.makeText(getContext(), "No data for " + topicName, Toast.LENGTH_SHORT).show();
        }
        adapter.updateData(dataList);
    }
}