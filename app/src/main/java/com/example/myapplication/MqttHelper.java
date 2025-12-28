package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson; // <-- Import Gson
import com.google.gson.JsonObject; // <-- Import JsonObject
import com.google.gson.JsonSyntaxException; // <-- Import for error handling
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;

public class MqttHelper {

    private static final String TAG = "MqttHelper";
    private Mqtt3AsyncClient client;

    public void connect(Context context) {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("android-client-" + System.currentTimeMillis())
                .serverHost("192.168.1.2")
                .serverPort(1883)
                .buildAsync();

        client.connect()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Connection failed", throwable);
                    } else {
                        Log.i(TAG, "Connected to MQTT");
                        subscribeToTopic("mobile/test", context);
                        // Optional: publish a test message if you want
                        // publishMessage("mobile/test", "{\"machine-name\":\"Test-Rig-01\", \"temperature\":45.5, \"speed\":1200, \"electricity-consumption\":1.23, \"timestamp\":1672531200}");
                    }
                });
    }

    public void subscribeToTopic(String topic, Context context) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish ->
                        // Call the new method to parse and save the message
                        parseAndSaveMessage(new String(publish.getPayloadAsBytes()), context)
                )
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Subscription failed", throwable);
                    } else {
                        Log.i(TAG, "Subscribed to: " + topic);
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
                        Log.e(TAG, "Publish failed", throwable);
                    } else {
                        Log.i(TAG, "Message published");
                    }
                });
    }

    public void disconnect() {
        if (client != null && client.getState() == MqttClientState.CONNECTED) {
            client.disconnect();
        }
    }

    /**
     * Parses a JSON string from MQTT, extracts the attributes, and saves them to the database.
     * @param jsonPayload The JSON string received from the MQTT message.
     * @param context The application context for accessing the database.
     */
    private void parseAndSaveMessage(String jsonPayload, Context context) {
        Log.i(TAG, "Received payload: " + jsonPayload);
        Gson gson = new Gson();
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Use Gson to parse the JSON string into a JsonObject
            JsonObject jsonObject = gson.fromJson(jsonPayload, JsonObject.class);

            // Extract each attribute from the JsonObject
            String machineName = jsonObject.get("machine-name").getAsString();
            double temperature = jsonObject.get("temperature").getAsDouble();
            int speed = jsonObject.get("speed").getAsInt();
            double electricityConsumption = jsonObject.get("electricity-consumption").getAsDouble();
            long timestamp = jsonObject.get("timestamp").getAsLong();

            // Put the data into ContentValues
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MACHINE_NAME, machineName);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TEMPERATURE, temperature);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SPEED, speed);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ELECTRICITY_CONSUMPTION, electricityConsumption);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP, timestamp);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

            if (newRowId == -1) {
                Log.e("SQLiteInsert", "Data insertion failed.");
            } else {
                Log.d("SQLiteInsert", "Data insertion successful, Row ID: " + newRowId);
            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Failed to parse JSON payload: " + jsonPayload, e);
        } catch (Exception e) {
            Log.e(TAG, "An error occurred during message saving.", e);
        } finally {
            // Always close the database connection
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }
}
