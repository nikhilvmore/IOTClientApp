package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentTopicListBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class TopicListFragment extends Fragment {
    private FragmentTopicListBinding binding;

    public static TopicMessagesFragment newInstance(String topic) {
        TopicMessagesFragment fragment = new TopicMessagesFragment();
        Bundle args = new Bundle();
        args.putString("topic", topic);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopicListBinding.inflate(inflater, container, false);

        List<String> activeTopics = new ArrayList<>();
        activeTopics.add("mobile/test");
        activeTopics.add("sensor/data");

        // FIX: Use requireActivity() here
        TopicPagerAdapter pagerAdapter = new TopicPagerAdapter((AppCompatActivity) requireActivity(), activeTopics);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(activeTopics.get(position))
        ).attach();

        return binding.getRoot();
    }
}
