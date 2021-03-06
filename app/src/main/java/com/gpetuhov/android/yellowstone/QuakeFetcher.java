package com.gpetuhov.android.yellowstone;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;
import com.gpetuhov.android.yellowstone.utils.UtilsMap;
import com.gpetuhov.android.yellowstone.utils.UtilsPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;


// Fetches JSON string with list of earthquakes from requested URL
public class QuakeFetcher {

    // Keeps instance of Retrofit
    private Retrofit mRetrofit;

    // Keeps instance of content resolver
    private ContentResolver mContentResolver;

    // Keeps instance of UtilsPrefs
    private UtilsPrefs mUtilsPrefs;

    // JSON response from USGS server
    private String mJsonResponse;

    // List of quakes content values
    private List<ContentValues> mQuakeListContentValues;

    // USGS API interface to be used in Retrofit
    private interface QuakeFetchService {
        // For USGS query parameters see http://earthquake.usgs.gov/fdsnws/event/1/
        // If magnitude and number of days are not specified,
        // the server returns all magnitudes for the last 30 days.

        @GET("query")   // USGS URL for queries is http://earthquake.usgs.gov/fdsnws/event/1/query
        Call<ResponseBody> getQuakes(
                @Query("format") String format,             // Response format
                @Query("latitude") String latitude,         // Area latitude
                @Query("longitude") String longitude,       // Area longitude
                @Query("maxradiuskm") String maxradiuskm);  // Area radius in kilometers
    }

    public QuakeFetcher(Retrofit retrofit, ContentResolver contentResolver, UtilsPrefs utilsPrefs) {
        mRetrofit = retrofit;
        mContentResolver = contentResolver;
        mUtilsPrefs = utilsPrefs;
    }

    // Fetch list of earthquakes from USGS server
    public void fetchQuakes() {
        mJsonResponse = getJsonResponse();
        parseJsonResponse();
        saveData();
    }

    // Return JSON response from USGS server
    private String getJsonResponse() {
        // String for the JSON response
        String jsonResponse = "";

        try {
            // Create instance of the USGS API interface implementation
            QuakeFetchService service = mRetrofit.create(QuakeFetchService.class);

            // Create call to USGS server
            Call<ResponseBody> call = service.getQuakes(
                    "geojson",                      // Response format = GeoJSON
                    UtilsMap.CALDERA_LATITUDE,    // Latitude of caldera
                    UtilsMap.CALDERA_LONGITUDE,   // Longitude of caldera
                    UtilsMap.CALDERA_RADIUS       // Radius of caldera
            );

            // Execute call synchronously (all QuakeFetcher must be run in background thread).
            // If no converter is specified, Retrofit returns OkHttp ResponseBody.
            Response<ResponseBody> response = call.execute();

            // Check if the call returned something
            if (response != null) {
                // Get OkHttp ResponseBody from Retrofit Response and convert it to String
                jsonResponse = response.body().string();
            }
        } catch (IOException e) {
            // If the call failed, return empty string
            jsonResponse = "";
        }

        return jsonResponse;
    }

    // Parse JSON response from USGS server,
    // save fetched data into list
    private void parseJsonResponse() {
        // Create new empty ArrayList for ContentValues of the earthquakes
        mQuakeListContentValues = new ArrayList<>();

        try {
            // Create JSONObject from JSON string
            JSONObject jsonBody = new JSONObject(mJsonResponse);

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

                // Add ContentValues for the new Quake object into the list
                mQuakeListContentValues.add(quake.getQuakeContentValues());
            }

        } catch (JSONException e) {
            // We don't have to catch JSONExceptions specifically,
            // because later we check if quakes content values list is not empty.
        }
    }

    // Save fetched data and
    // and set new quakes fetched flag in SharedPreferences
    // (this is needed for new earthquakes notifications).
    private void saveData() {
        // If quakes content values list is not empty
        if (mQuakeListContentValues.size() > 0) {
            // Get content values of the most recent quake
            ContentValues mostRecentQuakeCV = mQuakeListContentValues.get(0);

            // Get ID of the most recent quake
            String resultId = mostRecentQuakeCV.getAsString(QuakeEntry.COLUMN_IDS);

            // If ID of the most recent quake is not null
            if (resultId != null) {

                // Get ID of the most recent earthquake from SharedPreferences
                String lastResultID = mUtilsPrefs.getLastResultId();

                // If ID of the most recent earthquake in just fetched list
                // is not equal to the ID of the most recent earthquake in last time fetched list
                if (!resultId.equals(lastResultID)) {
                    // New quakes fetched

                    // Set new quakes fetched flag in SharedPreferences to "true"
                    mUtilsPrefs.setNewQuakesFetchedFlag(true);

                    // Save fetched data
                    updateQuakeStorage();
                } else {
                    // No new quakes fetched
                    // Set new quakes fetched flag in SharedPreferences to "false"
                    mUtilsPrefs.setNewQuakesFetchedFlag(false);
                }

                // Update ID of the most recent quake in SharedPreferences (replace with new value)
                mUtilsPrefs.setLastResultId(resultId);
            }
        }
    }

    // Replace old data in quake table with new fetched list
    private void updateQuakeStorage() {
        // Delete all rows from quake table (remove previously fetched data).
        // Method returns number of rows deleted, but we don't use it.
        mContentResolver.delete(QuakeEntry.CONTENT_URI, null, null);

        // Create new array of ContentValues of the proper size
        ContentValues[] quakesContentValuesArray = new ContentValues[mQuakeListContentValues.size()];

        // Convert list of quake content values to array of quake content values
        mQuakeListContentValues.toArray(quakesContentValuesArray);

        // Bulk insert this array into quake table
        mContentResolver.bulkInsert(QuakeEntry.CONTENT_URI, quakesContentValuesArray);
    }
}
