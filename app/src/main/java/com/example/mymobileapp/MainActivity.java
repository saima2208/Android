package com.example.mymobileapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mymobileapp.service.MyBackgroundService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String CUSTOM_BROADCAST_ACTION = "com.example.mymobileapp.CUSTOM_ACTION";

    private BroadcastReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button startServiceButton = findViewById(R.id.startButton);
        Button stopServiceButton = findViewById(R.id.buttonStop);
        Button sendBroadcastButton = findViewById(R.id.buttonSend);

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (CUSTOM_BROADCAST_ACTION.equals(intent.getAction())) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(context, "Broadcast Received: " + message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Custom Broadcast Received: " + message);
                }

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(CUSTOM_BROADCAST_ACTION));

        startServiceButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(MainActivity.this, MyBackgroundService.class);
            startService(serviceIntent);
            Toast.makeText(MainActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Attempting to start myBackgroundService");
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, MyBackgroundService.class);
                stopService(serviceIntent);
                Toast.makeText(MainActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Attempting to stop myBackgroundService");
            }
        });

        sendBroadcastButton.setOnClickListener(v -> {
            Intent broadcastIntent = new Intent(CUSTOM_BROADCAST_ACTION);
            broadcastIntent.putExtra("message", "Hello from MainActivity!");
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(broadcastIntent);
            Toast.makeText(MainActivity.this, "Custom Broadcast sent", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Custom Broadcast sent from MainActivity");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        Log.d(TAG,"MainActivity Destroyed,BroadcastReceiver unregistered ");
    }
}