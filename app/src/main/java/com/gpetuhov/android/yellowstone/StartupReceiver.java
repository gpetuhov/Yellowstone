package com.gpetuhov.android.yellowstone;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


// Broadcast receiver receives broadcast intent on system startup
// and activates quake polling service.
public class StartupReceiver extends BroadcastReceiver {

    // Method is called when broadcast receiver receives broadcast intent
    @Override
    public void onReceive(Context context, Intent intent) {

        // Activate quake polling service depending on new quake notifications settings
        QuakePollService.setServiceAlarm(context);
    }
}
