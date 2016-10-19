package com.gpetuhov.android.yellowstone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;


// Service checks for new earthquakes and sends notifications to user
public class QuakePollService extends IntentService {

    // Action constant for outgoing intent to show notification
    public static final String ACTION_SHOW_NOTIFICATION =
            "com.gpetuhov.android.yellowstone.SHOW_NOTIFICATION";

    // Our own permission for outgoing intents from the service.
    // Only components that use the same permission will be able to receive these intents.
    public static final String PERM_PRIVATE =
            "com.gpetuhov.android.yellowstone.PRIVATE";

    // Polling interval 60 seconds (for testing)
    public static final long POLL_INTERVAL_60SEC = 1000 * 60;

    // Polling interval 1 hour (for release)
    public static final long POLL_INTERVAL_HOUR = AlarmManager.INTERVAL_HOUR;

    // Polling interval in milliseconds
    public static final long POLL_INTERVAL = POLL_INTERVAL_60SEC;

    // Key for notification request code in outgoing intent
    public static final String REQUEST_CODE = "REQUEST_CODE";

    // Key for notification in outgoing intent
    public static final String NOTIFICATION = "NOTIFICATION";

    // ID of new quake notification. Older notifications are replaced by new with the same ID.
    public static final int QUAKE_NOTIFICATION_ID = 0;

    // Tag for logging
    private static final String LOG_TAG = QuakePollService.class.getName();

    // Create new intent to start this service
    public static Intent newIntent(Context context) {
        return new Intent(context, QuakePollService.class);
    }

    // Set AlarmManager to start or stop this service depending on settings in SharedPreferences
    public static void setServiceAlarm(Context context) {

        // Create new intent to start this service
        Intent i = QuakePollService.newIntent(context);

        // Get pending intent with this intent.
        // If pending intent for such intent already exists,
        // getService returns reference to it.
        // Otherwise new pending intent is created.
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        // Get reference to AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Get quake poll interval (in hours) from SharedPreferences
        int pollInterval = Integer.parseInt(
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(context.getString(R.string.pref_refresh_quake_key),
                                context.getString(R.string.pref_refresh_quake_value_1))
        );

        // If quake poll interval != 0 (not set to "never")
        if (pollInterval != 0) {

            // Poll interval in milliseconds
            long pollIntervalMillis = pollInterval * POLL_INTERVAL_HOUR;

            // Turn on AlarmManager for inexact repeating
            // (every poll interval AlarmManager will send pending request to start this service).
            // Time based is set to elapsed time since last system startup.
            // First time AlarmManager will trigger after first poll interval from current time.
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), pollIntervalMillis, pi);

        } else {
            // Otherwise (poll interval is set to "never") turn AlarmManager off

            // Cancel AlarmManager
            alarmManager.cancel(pi);

            // Cancel pending intent
            pi.cancel();
        }
    }

    public QuakePollService() {
        super(LOG_TAG);
    }

    // Method is called when new intent from the queue is ready to be handled
    @Override
    protected void onHandleIntent(Intent intent) {

        // Fetch new list of quakes from the network.
        // Method needs context to work, so we pass this service into it,
        // because Service is a Context.
        new QuakeFetcher().fetchQuakes(this);

        // If new quakes fetched flag in SharedPreferences is "true"
        if (QuakeUtils.getNewQuakesFetchedFlag(this)) {
            // Got a new result

            // Post log statement
            Log.i(LOG_TAG, "Got a new result");

            // Get reference to resources
            Resources resources = getResources();

            // Create new intent to start MainActivity
            Intent i = MainActivity.newIntent(this);

            // Create new pending intent with this intent to start new activity
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            // Build new notification
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.quake_notification_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.quake_notification_title))
                    .setContentText(resources.getString(R.string.quake_notification_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            // Send ordered broadcast with this notification
            showBackgroundNotification(QUAKE_NOTIFICATION_ID, notification);

        } else {
            // Otherwise got and old result, do nothing, only post log statement
            Log.i(LOG_TAG, "Got an old result");
        }
    }

    // Create new outgoing broadcast intent and send ordered broadcast with it
    private void showBackgroundNotification(int requestCode, Notification notification) {

        // Create new outgoing intent for showing notification
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);

        // Put notification request code into intent
        i.putExtra(REQUEST_CODE, requestCode);

        // Put notification into intent
        i.putExtra(NOTIFICATION, notification);

        // Send ordered broadcast with this intent and set initial broadcast result to "OK".
        // If no receiver sets result to "canceled", notification will be shown by result receiver.
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }
}
