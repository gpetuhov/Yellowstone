package com.gpetuhov.android.yellowstone;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    // Build request URL to USGS server with specified parameters
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


    // Get JSON response from the requested URL
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


    // Parse JSON response from USGS server
    public List<Quake> parseJsonString(String jsonString) {

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

        // Return the list of earthquakes
        return quakes;
    }


    // Fetch list of earthquakes from USGS server
    public List<Quake> fetchQuakes() {

        // Build request URL (query to USGS server)
        String requestURL = buildRequestUrl();

        // Get JSON response from USGS server
        String jsonResponse = getJsonString(requestURL);

        // Parse JSON response and return list of earthquakes
        return parseJsonString(jsonResponse);
    }

}
