package com.gpetuhov.android.yellowstone;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


// Fetches JSON string with list of earthquakes from requested URL
public class QuakeFetcher {

    // Tag for log messages
    private static final String LOG_TAG = QuakeFetcher.class.getName();

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
        return "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02&minmagnitude=5";
    }

}
