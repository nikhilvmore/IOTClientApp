package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // Default configuration (User can override these via UI)
    private String DEFAULT_HOST = "192.168.1.2";
    private String DEFAULT_TOPIC = "mobile/test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Start the background MQTT service
        startMqttService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void startMqttService() {
        Intent serviceIntent = new Intent(this, MqttService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_set_host) {
            showMqttHostDialog();
            return true;
        } else if (itemId == R.id.action_set_topic) {
            showMqttTopicDialog();
            return true;
        } else if (itemId == R.id.action_publish_message) {
            showPublishMessageDialog();
            return true;
        }else if (itemId == R.id.action_view_logs) {
            startActivity(new Intent(this, LogsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMqttHostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set MQTT Broker IP");
        final EditText input = new EditText(this);
        input.setHint("e.g., 192.168.1.2");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        input.setText(prefs.getString("mqtt_host_ip", DEFAULT_HOST));

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newIp = input.getText().toString().trim();
            if (!newIp.isEmpty()) {
                prefs.edit().putString("mqtt_host_ip", newIp).apply();
                Toast.makeText(this, "IP Saved. Restart app to reconnect.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null).show();
    }

    private void showMqttTopicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Subscription Topic");
        final EditText input = new EditText(this);
        input.setHint("e.g., mobile/test");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        input.setText(prefs.getString("mqtt_topic", DEFAULT_TOPIC));

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String topic = input.getText().toString().trim();
            if (!topic.isEmpty()) {
                prefs.edit().putString("mqtt_topic", topic).apply();
                Toast.makeText(this, "Topic Saved.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null).show();
    }

    private void showPublishMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Publish Message");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText topicInput = new EditText(this);
        topicInput.setHint("Topic");
        final EditText msgInput = new EditText(this);
        msgInput.setHint("Message");

        layout.addView(topicInput);
        layout.addView(msgInput);
        builder.setView(layout);

        builder.setPositiveButton("Publish", (dialog, which) -> {
            String t = topicInput.getText().toString().trim();
            String m = msgInput.getText().toString().trim();
            if (!t.isEmpty() && !m.isEmpty()) {
                publishViaService(t, m);
            }
        });
        builder.setNegativeButton("Cancel", null).show();
    }

    private void publishViaService(String topic, String message) {
        Intent intent = new Intent(this, MqttService.class);
        intent.setAction(MqttService.ACTION_PUBLISH);
        intent.putExtra(MqttService.EXTRA_TOPIC, topic);
        intent.putExtra(MqttService.EXTRA_MESSAGE, message);
        startService(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
