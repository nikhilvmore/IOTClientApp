package com.example.myapplication;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
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

import java.util.ArrayList;
import java.util.List;


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

        // This part using ViewBinding is the standard for new projects, you can keep yours if you prefer
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

        // Your MqttHelper logic can remain
        // mqttHelper = new MqttHelper();
        // mqttHelper.connect(this);

        // REMOVE all the RecyclerView, dataList, and adapter setup from here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}