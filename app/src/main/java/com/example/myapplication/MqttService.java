package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MqttService extends Service {
    private static final String TAG = "MqttService";
    private static final String CHANNEL_ID = "MqttServiceChannel";
    private Mqtt3AsyncClient client;

    public static final String ACTION_PUBLISH = "com.example.myapplication.ACTION_PUBLISH";
    public static final String EXTRA_TOPIC = "com.example.myapplication.EXTRA_TOPIC";
    public static final String EXTRA_MESSAGE = "com.example.myapplication.EXTRA_MESSAGE";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle Publish Intent
        if (intent != null && ACTION_PUBLISH.equals(intent.getAction())) {
            String topic = intent.getStringExtra(EXTRA_TOPIC);
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (topic != null && message != null) {
                publishMessage(topic, message);
            }
            return START_NOT_STICKY;
        }

        // Standard Startup
        startForeground(1, createNotification());
        connectMqtt();
        return START_STICKY;
    }

    private void connectMqtt() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String host = prefs.getString("mqtt_host_ip", "192.168.1.2");
        String topic = prefs.getString("mqtt_topic", "mobile/test");

        client = Mqtt3Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(host)
                .serverPort(1883)
                .buildAsync();

        client.connectWith()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Connection failed: " + throwable.getMessage());
                    } else {
                        Log.d(TAG, "Connected to " + host);
                        subscribeToTopic(topic);
                    }
                });
    }

    private void subscribeToTopic(String topic) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    Log.d(TAG, "Received: " + payload);
                    // Handle message (e.g., Save to DB or Broadcast to UI)
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) Log.e(TAG, "Subscribe failed");
                    else Log.d(TAG, "Subscribed to " + topic);
                });
    }

    public void publishMessage(String topic, String message) {
        if (client == null) return;
        client.publishWith()
                .topic(topic)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .send()
                .whenComplete((publishResult, throwable) -> {
                    if (throwable != null) Log.e(TAG, "Publish failed");
                    else Log.d(TAG, "Published to " + topic);
                });
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MQTT Service")
                .setContentText("HiveMQ Client Active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "MQTT Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        if (client != null) client.disconnect();
        super.onDestroy();
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
