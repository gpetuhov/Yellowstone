package com.gpetuhov.android.yellowstone;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.preference.PreferenceManager;

import java.util.List;


// Service checks for new earthquakes and sends notifications to user
public class QuakePollService extends IntentService {

    // Tag for logging
    private static final String LOG_TAG = QuakePollService.class.getName();

    // Polling interval in milliseconds
    public static final long POLL_INTERVAL = 1000 * 60; // 60 seconds

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

        // Get quake notifications settings from SharedPreferences
        boolean isOn = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_quake_notify_key), false);

        // If quake notifications are on
        if (isOn) {

            // Turn on AlarmManager for inexact repeating
            // (every POLL_INTERVAL AlarmManager will send pending request to start this service).
            // Time based is set to elapsed time since last system startup.
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);

        } else {
            // Otherwise turn AlarmManager off

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

        // Check if network is available and connected
        // Method needs context to work, so we pass this service into it,
        // because Service is a Context.
        if (QuakeUtils.isNetworkAvailableAndConnected(this)) {

            // Get ID of the most recent earthquake from SharedPreferences
            String lastResultID = QuakeUtils.getLastResultId(this);

            // Fetch new list of quakes from the network
            // TODO: Change to fetchQuakes after tests
            List<Quake> quakes = new QuakeFetcher().fetchAllWorldQuakes(this);

            // If quake list is empty, return
            if (quakes.size() == 0) {
                return;
            }

            // Get ID of the most recent earthquake in the list
            String resultId = quakes.get(0).getId();

            // If ID of the most recent earthquake in just fetched list
            // is not equal to the ID of the most recent earthquake in last time fetched list
            if (!resultId.equals(lastResultID)) {
                // Got a new result

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

                // Get notification manager
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // Display notification
                notificationManager.notify(0, notification);
            }
        }
    }
}
