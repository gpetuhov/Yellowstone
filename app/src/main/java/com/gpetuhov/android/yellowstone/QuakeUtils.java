package com.gpetuhov.android.yellowstone;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpetuhov.android.yellowstone.data.QuakeCursorWrapper;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Utilities class
public class QuakeUtils {

    // Caldera location on map
    public static final String CALDERA_LATITUDE = "44.5";       // Latitude of the center of caldera (degrees)
    public static final String CALDERA_LONGITUDE = "-110.6";    // Longitude of the center of caldera (degrees)
    public static final String CALDERA_RADIUS = "40";           // Radius of caldera (in kilometers)

    // Shifts from Caldera to include on map
    public static final double LAT_SHIFT = 0.5;
    public static final double LNG_SHIFT = 0.6;

    // Key for the most recent earthquake ID in SharedPreferences
    public static final String PREF_LAST_RESULT_ID = "last_result_id";

    // Return Caldera latitude converted to double
    public static double getCalderaLatDouble() {
        return Double.parseDouble(CALDERA_LATITUDE);
    }

    // Return Caldera longitude converted to double
    public static double getCalderaLngDouble() {
        return Double.parseDouble(CALDERA_LONGITUDE);
    }

    // Updates map with bounds including Caldera and points earthquakes
    // If quake is passed (not null), displays it on map.
    // If not (quake == null), displays all earthquakes from quake table.
    public static void updateMap(Context context, GoogleMap googleMap, Quake quake) {

        // Do nothing, if map is not ready
        if (googleMap == null) {
            return;
        }

        // Stores list of earthquakes to display on map
        List<Quake> quakes;

        if (quake == null) {
            // If quake is not passed, get all quakes from the quakes table
            quakes = getQuakes(context);
        } else {
            // Otherwise create empty list and add passed quake to it
            // In this case list contains only one quake to display
            quakes = new ArrayList<>();
            quakes.add(quake);
        }

        // Coordinates of Caldera
        double calderaLat = getCalderaLatDouble();
        double calderaLng = getCalderaLngDouble();

        // North shift from Caldera
        LatLng northOfCaldera = new LatLng(calderaLat + LAT_SHIFT, calderaLng);

        // South shift from Caldera
        LatLng southOfCaldera = new LatLng(calderaLat - LAT_SHIFT, calderaLng);

        // West shift from Caldera
        LatLng westOfCaldera = new LatLng(calderaLat, calderaLng - LNG_SHIFT);

        // East shift from Caldera
        LatLng eastOfCaldera = new LatLng(calderaLat, calderaLng + LNG_SHIFT);

        // Clear map
        googleMap.clear();

        // New latitude longitude bounds builder
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

        // Include shifts from Caldera to bounds builder
        latLngBoundsBuilder
                .include(northOfCaldera)
                .include(southOfCaldera)
                .include(westOfCaldera)
                .include(eastOfCaldera);

        // For each earthquake from the list
        for (Quake quakeToDisplay : quakes) {

            // Create point of the earthquake
            LatLng pointOfQuake = new LatLng(quakeToDisplay.getLatitude(), quakeToDisplay.getLongitude());

            // Build marker for the point of the earthquake
            MarkerOptions quakeMarker = new MarkerOptions().position(pointOfQuake);

            // Add marker for the earthquake to map
            googleMap.addMarker(quakeMarker);

            latLngBoundsBuilder.include(pointOfQuake);
        }

        // Build bounds from bounds builder
        LatLngBounds bounds = latLngBoundsBuilder.build();

        // Get map margin size from XML
        int margin = context.getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

        // Build camera update with the bounds built above and map margins
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, margin);

        // Move camera to the update built above
        googleMap.moveCamera(cameraUpdate);
    }

    // Return "true" if network is available and connected
    public static boolean isNetworkAvailableAndConnected(Context context) {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // True if network is available and connected
        boolean isNetworkConnected = networkInfo != null && networkInfo.isConnected();

        return isNetworkConnected;
    }

    // Get JSON response from the requested URL
    public static String getJsonString(String requestedUrl, String logTag) {

        // Create new OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Build new request from requested URL
        Request request = new Request.Builder()
                .url(requestedUrl)
                .build();

        String jsonResponse = null;  // String contains JSON response

        Response response = null;   // OkHttp response

        try {
            // Get response from server
            response = client.newCall(request).execute();
            // Convert response to string
            jsonResponse = response.body().string();
        } catch (IOException e) {
            // Nothing to return
            Log.e(logTag, "Error fetching JSON string", e);
            return null;
        }

        // Shutdown for OkHttp isn't necessary

        return jsonResponse;
    }


    // Return ID of the most recent fetched earthquake from SharedPreferences
    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT_ID, null);
    }


    // Set new value for the ID of the most recent fetched earthquake in SharedPreferences
    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }


    // Return content values to write Quake object into database
    public static ContentValues getQuakeContentValues(Quake quake) {

        // Create new content values
        ContentValues values = new ContentValues();

        // Put data from Quake object fields into content values
        values.put(QuakeEntry.COLUMN_IDS, quake.getId());
        values.put(QuakeEntry.COLUMN_MAGNITUDE, quake.getMagnitude());
        values.put(QuakeEntry.COLUMN_LOCATION, quake.getLocation());
        values.put(QuakeEntry.COLUMN_LATITUDE, quake.getLatitude());
        values.put(QuakeEntry.COLUMN_LONGITUDE, quake.getLongitude());
        values.put(QuakeEntry.COLUMN_DEPTH, quake.getDepth());
        values.put(QuakeEntry.COLUMN_TIME, quake.getTimeInMilliseconds());
        values.put(QuakeEntry.COLUMN_URL, quake.getUrl());

        return values;
    }


    // Return list of earthquakes
    public static List<Quake> getQuakes(Context context) {

        // Create new empty list of quakes
        List<Quake> quakes = new ArrayList<>();

        // Get content resolver for the context, query for all quakes
        // and save received cursor.
        Cursor cursor = context.getContentResolver().query(
                QuakeEntry.CONTENT_URI, // URI
                null,                   // Projection
                null,                   // Selection
                null,                   // Selection arguments
                null                    // Sort order
        );

        // If the cursor is null, return empty list of quakes
        if (cursor == null) {
            return quakes;
        }

        // Create quake cursor wrapper upon the received cursor
        QuakeCursorWrapper cursorWrapper = new QuakeCursorWrapper(cursor);

        try {
            // Move to the first row of the cursor
            cursorWrapper.moveToFirst();

            // While we didn't move after the last row of the cursor
            while (!cursorWrapper.isAfterLast()) {

                // Extract Quake object from the cursor row and add it to list of quakes
                quakes.add(cursorWrapper.getQuake());

                // Move to the next row of the cursor
                cursorWrapper.moveToNext();
            }

        } finally {
            // Always close cursors, if not closed, to prevent memory leaks
            if (!cursorWrapper.isClosed()) {
                cursorWrapper.close();
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        return quakes;
    }


    // Return Quake object with specified ID from the database table
    public static Quake getQuake(Context context, long quakeDbId) {

        // Get content resolver for the application context, query for the quake with specified ID
        // and save received cursor.
        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(QuakeEntry.CONTENT_URI, String.valueOf(quakeDbId)),
                // Build URI (scheme://content_authority/table_name/row_id)
                null,                   // Projection
                null,                   // Selection
                null,                   // Selection arguments
                null                    // Sort order
        );

        // If the cursor is null, return null
        if (cursor == null) {
            return null;
        }

        // Create quake cursor wrapper upon the received cursor
        QuakeCursorWrapper cursorWrapper = new QuakeCursorWrapper(cursor);

        try {
            // If there are no rows in the response cursor
            if (cursorWrapper.getCount() == 0) {
                // There is no earthquake with specified ID in the database, nothing to return
                return null;
            }

            // Move to the first row of the cursor
            cursorWrapper.moveToFirst();

            // Extract Quake object from the cursor row and return it
            return cursorWrapper.getQuake();

        } finally {
            // Always close cursors, if not closed, to prevent memory leaks
            if (!cursorWrapper.isClosed()) {
                cursorWrapper.close();
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
