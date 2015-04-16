package com.example.root.ias_test;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by root on 9/4/15.
 */     public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dailyUpdater = new Intent(context, MyService.class);

        context.startService(dailyUpdater);

        Log.e("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
    }

}