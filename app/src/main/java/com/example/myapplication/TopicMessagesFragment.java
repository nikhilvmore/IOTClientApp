package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TopicMessagesFragment extends Fragment {
    private String topic;
    private MessageAdapter adapter;

    public static TopicMessagesFragment newInstance(String topic) {
        TopicMessagesFragment fragment = new TopicMessagesFragment();
        Bundle args = new Bundle();
        args.putString("topic", topic);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        topic = getArguments().getString("topic");
        RecyclerView rv = new RecyclerView(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(); // Create a standard RecyclerView Adapter
        rv.setAdapter(adapter);
        return rv;
    }

    // Call this from MainActivity when a new message arrives
    public void addMessage(String message) {
        adapter.addMessage(message);
    }
}
