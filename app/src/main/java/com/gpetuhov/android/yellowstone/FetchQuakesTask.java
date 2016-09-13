package com.gpetuhov.android.yellowstone;

import android.os.AsyncTask;
import android.util.Log;


// Background thread for fetching list of earthquakes from request URL
public class FetchQuakesTask extends AsyncTask<Void, Void, Void> {

    // Tag for log messages
    public static final String LOG_TAG = FetchQuakesTask.class.getName();

    @Override
    protected Void doInBackground(Void... params) {

        QuakeFetcher quakeFetcher = new QuakeFetcher();

        // Build request URL
        String requestUrl = quakeFetcher.buildRequestUrl();

        // Get JSON string from request URL
        String result = quakeFetcher.getJsonString(requestUrl);

        Log.i(LOG_TAG, "Fetched contents of URL" + result);

        return null;
    }
}
