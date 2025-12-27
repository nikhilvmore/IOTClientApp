package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;

public class MqttHelper {

    private Mqtt3AsyncClient client;

    public void connect(Context context) {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("android-client-" + System.currentTimeMillis())
                .serverHost("192.168.1.4")
                .serverPort(1883)
                .buildAsync();

        client.connect()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        System.out.println("Connected to MQTT");
                        subscribeToTopic("mobile/test", context);
                        publishMessage("mobile/test", "Hello MQTT from Android111!");
                    }
                });
    }

    public void subscribeToTopic(String topic, Context context) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish ->
                        saveNewEntry(new String(publish.getPayloadAsBytes()),"tests", context)
                )
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        System.out.println("Subscribed to: " + topic);
                    }
                });

    }

    public void publishMessage(String topic, String message) {
        client.publishWith()
                .topic(topic)
                .payload(message.getBytes())
                .send()
                .whenComplete((pubAck, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        System.out.println("Message published");
                    }
                });
    }

    public void disconnect() {
        if (client != null && client.getState() == MqttClientState.CONNECTED) {
            client.disconnect();
        }
    }

    public long saveNewEntry(String title, String subtitle, Context context) {
        //System.out.println("Received: " +
        //        new String(publish.getPayloadAsBytes()));
        System.out.println("Received: " +title);
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        long newRowId = db.insert(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                null,
                values
        );

        dbHelper.close();

        if (newRowId == -1) {
            Log.e("SQLiteInsert", "Data insertion failed.");
        } else {
            Log.d("SQLiteInsert", "Data insertion successful, Row ID: " + newRowId);
        }

        return newRowId;
    }
}
