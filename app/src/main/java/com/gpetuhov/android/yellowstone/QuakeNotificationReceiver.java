package com.gpetuhov.android.yellowstone;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;


// Receiver of the result of ordered broadcast sent by quake polling service.
// Priority of this receiver is the least of all receivers in the app,
// so this receiver is the last to receive broadcast from quake polling service.
// This receiver checks result of the broadcast and shows new quake notification
// if the result is "OK" (which means that there are no visible activities of the app),
// otherwise does nothing.
public class QuakeNotificationReceiver extends BroadcastReceiver {

    // Method is called when broadcast intent is received.
    @Override
    public void onReceive(Context context, Intent intent) {

        // If no receiver set result of the broadcast to "canceled"
        // (which means that there are no visible activities of the app)
        if (getResultCode() == Activity.RESULT_OK) {
            // Get notification from incoming intent and display it

            // Get notification ID from incoming intent
            int requestCode = intent.getIntExtra(QuakePollService.REQUEST_CODE, 0);

            // Get notification from incoming intent
            Notification notification = (Notification)
                    intent.getParcelableExtra(QuakePollService.NOTIFICATION);

            // Get notification manager
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Display notification
            notificationManager.notify(requestCode, notification);
        }
    }
}
