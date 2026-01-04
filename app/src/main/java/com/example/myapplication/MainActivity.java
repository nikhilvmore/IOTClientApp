package com.example.myapplication;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
//import org.eclipse.paho.android.service.MqttAndroidClient;
import android.content.Context;

//import info.mqtt.android.service.Ack;
import org.eclipse.paho.android.service.MqttAndroidClient;
//import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttAndroidClient;
// and for other classes, use imports from org.eclipse.paho...

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.MqttException;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.IntentFilter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.ContentProvider;
import com.example.myapplication.MyAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;

import android.view.ViewGroup;
import android.widget.LinearLayout; // <-- Import LinearLayout



public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Context context;
    private MqttAndroidClient client;
    private String SERVER_URI = "tcp://192.168.1.4:1883";
    private String SUBSCRIPTION_TOPIC = "exampleAndroidTopic";
    private String PUBLISH_TOPIC = "exampleAndroidPublishTopic";
    private String PUBLISH_MESSAGE = "Hello World";
    private String clientId = "BasicSample" + System.currentTimeMillis();

    private ContentProvider provider;

    MqttHelper mqttHelper;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<MyData> dataList;

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

        startMqttService();
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
    protected void onDestroy() {
        super.onDestroy();
        // The service will handle its own lifecycle, but if you had a direct helper:
        // if (mqttHelper != null) mqttHelper.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // Inflate the menu to add the "Settings" option
    // Handle menu item clicks
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Handles clicks on menu items in the action bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Use a switch statement for better readability with multiple menu items.
        int itemId = item.getItemId();
        if (itemId == R.id.action_set_host) {
            showMqttHostDialog();
            return true;
        } else if (itemId == R.id.action_set_topic) {
            showMqttTopicDialog(); // <-- Call the new topic-specific dialog
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a dialog for the user to set the MQTT Broker IP address.
     */
    private void showMqttHostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set MQTT Broker IP");

        final EditText input = new EditText(this);
        input.setHint("e.g., 192.168.1.2");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Use the same key as your MqttService to read/write the correct value
        String currentHost = sharedPreferences.getString("mqtt_host_ip", "192.168.1.2");
        input.setText(currentHost);

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newHostIp = input.getText().toString().trim();
            if (!newHostIp.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mqtt_host_ip", newHostIp);
                editor.apply();
                Log.i("Settings", "New MQTT Host IP saved: " + newHostIp);
                // Inform the user that a restart or reconnect might be needed.
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Displays a dialog for the user to set the MQTT subscription topic.
     */
    private void showMqttTopicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set MQTT Topic");

        final EditText input = new EditText(this);
        input.setHint("e.g., mobile/test");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Use the same key as your MqttService to read/write the correct value
        String currentTopic = sharedPreferences.getString("mqtt_topic", "mobile/test");
        input.setText(currentTopic);

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTopic = input.getText().toString().trim();
            if (!newTopic.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mqtt_topic", newTopic);
                editor.apply();
                Log.i("Settings", "New MQTT Topic saved: " + newTopic);
                // Inform the user that a restart or reconnect might be needed.
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


}