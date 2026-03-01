package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MqttHelper;
import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private MqttHelper mqttHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mqttHelper = new MqttHelper();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
            // Use the simple layout with just buttons
            View root = inflater.inflate(R.layout.fragment_home, container, false);

            Button buttonStart = root.findViewById(R.id.button_start);
            Button buttonStop = root.findViewById(R.id.button_stop);
            Button buttonViewList = root.findViewById(R.id.button_view_list);

            buttonStart.setOnClickListener(v -> {
                mqttHelper.connect(getContext());
                Snackbar.make(v, "MQTT Connection Starting...", Snackbar.LENGTH_SHORT).show();
            });

            buttonStop.setOnClickListener(v -> {
                mqttHelper.disconnect();
                Snackbar.make(v, "MQTT Disconnected", Snackbar.LENGTH_SHORT).show();
            });

            // Use the action ID to navigate to the topic list page
            // This creates a proper back-stack so you can return to Home
            buttonViewList.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_topic_list);
            });

            return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.disconnect();
        }
    }
}