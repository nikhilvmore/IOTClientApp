package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttService extends Service {
    private static final String TAG = "MqttService";
    private static final String CHANNEL_ID = "MqttServiceChannel";
    private MqttAndroidClient mqttClient;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the notification for the foreground service
        Intent notificationIntent = new Intent(this, MainActivity.class); // Or your desired activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MQTT Service")
                .setContentText("Listening for messages...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your own icon
                .setContentIntent(pendingIntent)
                .build();

        // A service must call startForeground() within 5 seconds of being started
        startForeground(1, notification);

        // Initialize and connect the MQTT client
        connectMqtt();

        // If the service is killed, it will be automatically restarted.
        return START_STICKY;
    }

    private void connectMqtt() {
        String serverUri = "tcp://your_broker_address:1883"; // e.g., "tcp://broker.hivemq.com:1883"
        String clientId = "AndroidClient_" + System.currentTimeMillis();

        mqttClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true); // Set to false if you want persistent sessions

            IMqttToken token = mqttClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "MQTT Connection Success!");
                    // Connection is successful, you can subscribe to topics here
                    subscribeToTopic("your/topic");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "MQTT Connection Failure!", exception);
                    // Connection failed. You might want to schedule a retry here.
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Error connecting to MQTT", e);
        }
    }

    private void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribe(topic, 1, (topic1, message) -> {
                // This is where you handle incoming messages
                String payload = new String(message.getPayload());
                Log.d(TAG, "Message received on topic " + topic1 + ": " + payload);
                // You can send a broadcast intent to update your UI or process the data
            });
            Log.d(TAG, "Subscribed to topic: " + topic);
        } catch (MqttException e) {
            Log.e(TAG, "Error subscribing to topic", e);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Disconnect the client when the service is destroyed
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error disconnecting MQTT client", e);
        }
        Log.d(TAG, "MqttService destroyed");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "MQTT Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

