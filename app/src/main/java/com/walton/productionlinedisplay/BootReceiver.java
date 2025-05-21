package com.walton.productionlinedisplay;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    String packageName = "com.walton.productionlinedisplay";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: BootReceiver");
        context.startService(new Intent(context, NotifyingDailyService.class));
//        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//        if (launchIntent != null){
//            Log.d(TAG, "onReceive: app is starting");
//            context.startActivity(launchIntent);
//        }else {
//            Log.d(TAG, "onReceive: App is not installed");
//        }
    }
}
