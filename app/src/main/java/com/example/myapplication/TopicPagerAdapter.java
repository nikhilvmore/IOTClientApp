package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.ui.list.ListFragment;

import java.util.List;

public class TopicPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    private final List<String> topics;

    public TopicPagerAdapter(AppCompatActivity activity, List<String> topics) {
        super(activity);
        this.topics = topics;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Now this will find the method correctly
        return ListFragment.newInstance(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }
}
