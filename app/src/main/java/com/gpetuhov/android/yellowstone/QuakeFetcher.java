package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


// Fetches JSON string with list of earthquakes from requested URL
public class QuakeFetcher {

    // Tag for log messages
    private static final String LOG_TAG = QuakeFetcher.class.getName();

    // USGS URL for queries
    public static final String USGS_QUERY_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";

    // Build default request URL to USGS server
    public String buildDefaultRequestUrl() {

        // For USGS query parameters see http://earthquake.usgs.gov/fdsnws/event/1/

        // Default query: last 30 days, all magnitudes
        final String defaultUrl = Uri.parse(USGS_QUERY_URL)
                .buildUpon()
                .appendQueryParameter("format", "geojson")  // Response format = GeoJSON
                .appendQueryParameter("latitude", QuakeUtils.CALDERA_LATITUDE)     // Latitude of caldera
                .appendQueryParameter("longitude", QuakeUtils.CALDERA_LONGITUDE)   // Longitude of caldera
                .appendQueryParameter("maxradiuskm", QuakeUtils.CALDERA_RADIUS)    // Radius of caldera
                .build().toString();

        return defaultUrl;
    }

    // Build request URL to USGS server with specified parameters
    public String buildRequestUrl(Context context) {

        // Build default request URL
        final String defaultUrl = buildDefaultRequestUrl();

        // Get default SharedPreferences
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        // Get magnitude preference value from SharedPreference by the key
        // (default value is value_1 (minimum magnitude = 0, that is all magnitudes)
        String minMagnitude = sharedPrefs.getString(
                context.getString(R.string.pref_magnitude_key),
                context.getString(R.string.pref_magnitude_value_1));

        // Build request URL upon default request URL
        final String requestUrl = Uri.parse(defaultUrl)
                .buildUpon()
                .appendQueryParameter("minmagnitude", minMagnitude)  // Minimum magnitude
                .build().toString();

        return requestUrl;
    }

    // Build request URL for all earthquakes in the world since specified date
    private String buildAllWorldRequestUrl() {

        final String requestUrl = Uri.parse(USGS_QUERY_URL)
                .buildUpon()
                .appendQueryParameter("format", "geojson")  // Response format = GeoJSON
                .appendQueryParameter("starttime", "2016-10-03")  // Response format = GeoJSON
                .build().toString();

        return requestUrl;
    }

    // Parse JSON response from USGS server
    // and save ID of the most recent earthquake to SharedPreferences
    // (this is needed for new earthquakes notifications).
    public List<Quake> parseJsonString(Context context, String jsonString) {

        // Empty ArrayList for the list of earthquakes
        List<Quake> quakes = new ArrayList<>();

        try {

            // Create JSONObject from JSON string
            JSONObject jsonBody = new JSONObject(jsonString);

            // Extract JSONArray associated with the key called "features" (feature = earthquake)
            JSONArray quakeJsonArray = jsonBody.getJSONArray("features");

            // For each earthquake in the quakeArray create a Quake object
            for (int i = 0; i < quakeJsonArray.length(); i++) {

                // Get a single earthquake at position i
                JSONObject quakeJsonObject = quakeJsonArray.getJSONObject(i);

                // Extract ID
                String id = quakeJsonObject.getString("id");

                // JSONObject with key "properties" represents a list of all properties for the earthquake
                JSONObject quakePropertiesJsonObject = quakeJsonObject.getJSONObject("properties");

                // Extract magnitude
                double magnitude = quakePropertiesJsonObject.getDouble("mag");

                // Extract location name
                String location = quakePropertiesJsonObject.getString("place");

                // Extract time
                long time = quakePropertiesJsonObject.getLong("time");

                // Extract URL with earthquake details
                String url = quakePropertiesJsonObject.getString("url");

                // JSONObject with key "geometry" represents point of the earthquake
                JSONObject quakeGeometryJsonObject = quakeJsonObject.getJSONObject("geometry");

                // JSONArray with key "coordinates" represents coordinates of the earthquake
                // [longitude, latitude, depth]
                JSONArray quakeCoordinatesJsonArray = quakeGeometryJsonObject.getJSONArray("coordinates");

                // Extract longitude
                double longitude = quakeCoordinatesJsonArray.getDouble(0);

                // Extract latitude
                double latitude = quakeCoordinatesJsonArray.getDouble(1);

                // Extract depth
                double depth = quakeCoordinatesJsonArray.getDouble(2);

                // Create new Quake object with data from from the JSON response
                Quake quake = new Quake(id, magnitude, location, time, url, latitude, longitude, depth);

                // Add new Quake object to the list of earthquakes
                quakes.add(quake);
            }

        } catch (JSONException e) {
            // Catch JSON parsing errors
            Log.e(LOG_TAG, "Failed to parse JSON", e);
        }

        // If list of quakes is not empty
        if (quakes.size() > 0) {

            // Get ID of the most recent earthquake
            String resultId = quakes.get(0).getId();

            // Save this ID to SharedPreferences
            QuakeUtils.setLastResultId(context, resultId);
        }

        // Return the list of earthquakes
        return quakes;
    }


    // Fetch list of earthquakes from USGS server
    public List<Quake> fetchQuakes(Context context) {

        // Build request URL (query to USGS server)
        String requestURL = buildRequestUrl(context);

        // Get JSON response from USGS server
        String jsonResponse = QuakeUtils.getJsonString(requestURL, LOG_TAG);

        // Parse JSON response and save list of earthquakes
        return parseJsonString(context, jsonResponse);
    }


    // Fetch list of all earthquakes in the world since 2016-09-28 from USGS server
    public List<Quake> fetchAllWorldQuakes(Context context) {

        // Build request URL (query to USGS server)
        String requestURL = buildAllWorldRequestUrl();

        // Get JSON response from USGS server
        String jsonResponse = QuakeUtils.getJsonString(requestURL, LOG_TAG);

        // Parse JSON response and save list of earthquakes
        return parseJsonString(context, jsonResponse);
    }

}
