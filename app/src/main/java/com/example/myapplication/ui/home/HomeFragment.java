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
        // Initialize MqttHelper. It's better to do this in onCreate
        // to avoid re-creating it every time the view is created.
        mqttHelper = new MqttHelper();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Find all buttons
        final Button buttonStart = root.findViewById(R.id.button_start);
        final Button buttonStop = root.findViewById(R.id.button_stop);
        final Button buttonViewList = root.findViewById(R.id.button_view_list);

        // Set OnClickListener for Start Button
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The 'getContext()' method provides the necessary Context
                mqttHelper.connect(getContext());
                Snackbar.make(v, "MQTT Connection Starting...", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for Stop Button
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHelper.disconnect();
                Snackbar.make(v, "MQTT Disconnected", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for View List Button (as before)
        buttonViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the action defined in the navigation graph to move to the list fragment
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_list);
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        // It's good practice to ensure disconnection when the fragment is destroyed
        // to prevent memory leaks.
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.disconnect();
        }
    }
}
