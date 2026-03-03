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
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    // Default configuration (User can override these via UI)
    private String DEFAULT_HOST = "192.168.1.4";
    private String DEFAULT_TOPIC = "mobile/test";

    private List<String> activeTopics = new ArrayList<>();
    private TopicPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // 1. Correct way to get NavController with FragmentContainerView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // 2. Define top-level destinations (Drawer hamburger shows here)
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();

            // 3. Setup UI components
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        }

        // --- DUPLICATE CODE REMOVED FROM HERE ---

        // Start the background MQTT service
        startMqttService();

        // Notification permission check for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Example: Load Dashboard on Startup
        // Instead of R.id.fragment_container, use the actual ID in your layout
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment_content_main, new DashboardFragment())
//                .commit();
       // setupDynamicColumns();
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
//        else if (itemId == R.id.action_add_column) {
//            showAddTopicColumnDialog();
//            return true;
//        }
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
        // Use the class-level navController variable initialized in onCreate
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setupDynamicColumns() {
        // 1. Initial topics (Ensure these match machine names in your DB)
        if (activeTopics.isEmpty()) {
            activeTopics.add("Machine A");
            activeTopics.add("Machine B");
        }

        // 2. Initialize Adapter
        pagerAdapter = new TopicPagerAdapter(this, activeTopics);

        // FIX: Access via binding.appBarMain.viewPager and binding.appBarMain.tabLayout
        binding.appBarMain.viewPager.setAdapter(pagerAdapter);

        // 3. Attach TabLayout to ViewPager
        new TabLayoutMediator(binding.appBarMain.tabLayout, binding.appBarMain.viewPager,
                (tab, position) -> tab.setText(activeTopics.get(position))
        ).attach();
    }

    private void showAddTopicColumnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Topic Column");
        final EditText input = new EditText(this);
        input.setHint("Topic Name");
        builder.setView(input);
        builder.setPositiveButton("Add", (d, w) -> {
            String newTopic = input.getText().toString().trim();
            if (!newTopic.isEmpty() && !activeTopics.contains(newTopic)) {
                activeTopics.add(newTopic);

                // Notify adapter of new data
                pagerAdapter.notifyItemInserted(activeTopics.size() - 1);

                // Re-attach TabLayoutMediator to refresh the tabs
//                new TabLayoutMediator(binding.appBarMain.tabLayout, binding.appBarMain.viewPager,
//                        (tab, position) -> tab.setText(activeTopics.get(position))
//                ).attach();

                Toast.makeText(this, "Column added for " + newTopic, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
