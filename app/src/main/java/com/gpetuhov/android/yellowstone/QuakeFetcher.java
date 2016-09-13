package com.gpetuhov.android.yellowstone;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


// Fetches JSON string with list of earthquakes from requested URL
public class QuakeFetcher {

    // Tag for log messages
    private static final String LOG_TAG = QuakeFetcher.class.getName();

    // USGS URL for queries
    public static final String USGS_QUERY_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";

    // Caldera location on map
    public static final String CALDERA_LATITUDE = "44.5";       // Latitude of the center of caldera (degrees)
    public static final String CALDERA_LONGITUDE = "-110.6";    // Longitude of the center of caldera (degrees)
    public static final String CALDERA_RADIUS = "40";           // Radius of caldera (in kilometers)

    public String getJsonString(String requestedUrl) {

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
            Log.e(LOG_TAG, "Error fetching JSON string", e);
            return null;
        }

        // Shutdown for OkHttp isn't necessary

        return jsonResponse;
    }

    // Build request URL with specified parameters
    public String buildRequestUrl() {

        // For USGS query parameters see http://earthquake.usgs.gov/fdsnws/event/1/

        // Default query: last 30 days, magnitude >= 2
        final String defaultUrl = Uri.parse(USGS_QUERY_URL)
                .buildUpon()
                .appendQueryParameter("format", "geojson")  // Response format = GeoJSON
                .appendQueryParameter("latitude", CALDERA_LATITUDE)     // Latitude of caldera
                .appendQueryParameter("longitude", CALDERA_LONGITUDE)   // Longitude of caldera
                .appendQueryParameter("maxradiuskm", CALDERA_RADIUS)    // Radius of caldera
                .appendQueryParameter("minmagnitude", "2")  // Minimum magnitude = 2
                .build().toString();

        return defaultUrl;
    }

}
