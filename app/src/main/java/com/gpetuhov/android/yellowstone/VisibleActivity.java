package com.gpetuhov.android.yellowstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;


// Abstract class for visible activity.
// All activities in the app extend this activity.
// When visible on screen VisibleActivity cancels ordered broadcast intent
// that shows notification of new quakes so that notification is shown
// only when user does not interact with the app.
// To do this VisibleActivity dynamically registers broadcast receiver.
public abstract class VisibleActivity extends AppCompatActivity {

    // Method is called when activity becomes visible
    @Override
    protected void onStart() {
        super.onStart();

        // Create new intent filter to receive only intents from quake polling service
        IntentFilter intentFilter = new IntentFilter(QuakePollService.ACTION_SHOW_NOTIFICATION);

        // Dynamically register broadcast receiver to receive intents from quake polling service.
        // When activity is visible, broadcast receiver becomes active
        // and intercepts intents from quake polling service.
        // Receiver uses our own permission to receive intents from quake polling service.
        registerReceiver(mOnShowNotification, intentFilter, QuakePollService.PERM_PRIVATE, null);
    }


    // Method is called when activity becomes not visible
    @Override
    protected void onStop() {
        super.onStop();

        // Dynamically unregister broadcast receiver.
        // When activity becomes not visible, broadcast receiver stops intercepting
        // intents from quake polling service.
        unregisterReceiver(mOnShowNotification);
    }


    // Create new broadcast receiver and save its reference in private field.
    // Receiver receives intent from quake polling service and cancels it.
    // So new quakes notification is not shown.
    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {

        // Method is called when intent is received
        @Override
        public void onReceive(Context context, Intent intent) {

            // Set result of the received broadcast to "canceled".
            // The broadcast is not aborted and proceeds to next receiver,
            // but with result set to "canceled"
            setResultCode(RESULT_CANCELED);
        }
    };

}
