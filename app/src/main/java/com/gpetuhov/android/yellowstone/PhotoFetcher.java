package com.gpetuhov.android.yellowstone;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Fetches list of photos from Flickr
public class PhotoFetcher {

    // Tag for log messages
    private static final String LOG_TAG = PhotoFetcher.class.getName();

    // Flickr URL for queries
    public static final String FLICKR_QUERY_URL = "https://api.flickr.com/services/rest/";

    // Build request URL to Flickr with specified parameters
    public String buildRequestUrl() {

        // For Flickr query parameters see https://www.flickr.com/services/api/

        // Default query: Search text = yellowstone, sorted by date uploaded, returns 100 photos
        // (this is default sort order and number of photos returned, if not specified)
        final String defaultUrl = Uri.parse(FLICKR_QUERY_URL)
                .buildUpon()
                // Method = search, because we search photos with "yellowstone" keyword
                .appendQueryParameter("method", "flickr.photos.search")
                // Flickr API key is added in app build.gradle file from global gradle.properties file
                .appendQueryParameter("api_key", BuildConfig.FLICKR_API_KEY)
                .appendQueryParameter("text", "yellowstone")    // Search keyword
                .appendQueryParameter("format", "json")         // Response format
                .appendQueryParameter("nojsoncallback", "1")    // Simplified JSON response
                .appendQueryParameter("extras", "url_s")        // Include image URL
                .build().toString();

        return defaultUrl;
    }

    // Parse JSON response from Flickr
    public List<PhotoListItem> parseJsonString(String jsonString) {

        // Empty ArrayList for the list of photos
        List<PhotoListItem> items = new ArrayList<>();

        try {

            // Create JSONObject from JSON string
            JSONObject jsonBody = new JSONObject(jsonString);

            // Extract JSONObject with key "photos"
            JSONObject photosJsonObject = jsonBody.getJSONObject("photos");

            // Extract JSONArray with key "photo" (this array contains photos)
            JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

            // For each photo in photoJsonArray
            for (int i = 0; i < photoJsonArray.length(); i++) {

                // Extract JSONObject at position i (this is one photo)
                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

                // Check if tha photo has URL (if not, ignore it)
                if (photoJsonObject.has("url_s")) {

                    // Create new PhotoListItem object to store photo details
                    PhotoListItem item = new PhotoListItem();

                    // Extract photo ID and store it in PhotoListItem object
                    item.setId(photoJsonObject.getString("id"));

                    // Extract photo Title and store it in PhotoListItem object
                    item.setCaption(photoJsonObject.getString("title"));

                    // Extract photo URL and store it in PhotoListItem object
                    item.setUrl(photoJsonObject.getString("url_s"));

                    // Extract photo owner and store it in PhotoListItem object
                    item.setOwner(photoJsonObject.getString("owner"));

                    // Add PhotoListItem object to photo list
                    items.add(item);
                }
            }

        } catch (JSONException e) {
            // Catch JSON parsing errors
            Log.e(LOG_TAG, "Failed to parse JSON", e);
        }

        // Return the list of photos
        return items;
    }


    // Fetch list of photos from Flickr
    public List<PhotoListItem> fetchPhotos() {

        // Build request URL (query to Flickr)
        String requestURL = buildRequestUrl();

        // Get JSON response from Flickr
        String jsonResponse = QuakeUtils.getJsonString(requestURL, LOG_TAG);

        // Parse JSON response and return list of photos
        return parseJsonString(jsonResponse);
    }

}
