package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyMessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MyMessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the broadcast message
        String action = intent.getAction();
        Log.d(TAG, "Broadcast received with action: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Message received: " + action);
        }

        // Example: Extract data from the Intent
        if (intent.hasExtra("message")) {
            String message = intent.getStringExtra("message");
            Log.d(TAG, "Message received: " + message);
        }
    }
}

