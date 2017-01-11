package com.gpetuhov.android.yellowstone;


import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Fetches list of photos from Flickr
public class PhotoFetcher {

    // Flickr URL for queries
    private static final String FLICKR_QUERY_URL = "https://api.flickr.com/services/rest/";

    // OkHttpClient to access network
    private OkHttpClient mOkHttpClient;

    public PhotoFetcher(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    // Fetch list of photos from Flickr
    public List<PhotoListItem> fetchPhotos() {

        // Build request URL (query to Flickr)
        String requestURL = buildRequestUrl();

        // Get JSON response from Flickr
        String jsonResponse = getJsonString(requestURL);

        // Parse JSON response and return list of photos
        return parseJsonString(jsonResponse);
    }

    // Build request URL to Flickr with specified parameters
    private String buildRequestUrl() {

        // For Flickr query parameters see https://www.flickr.com/services/api/

        // Default query: Search text = yellowstone, sorted by date uploaded, returns 100 photos
        // (this is default sort order and number of photos returned, if not specified)
        return Uri.parse(FLICKR_QUERY_URL)
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
    }

    // Get JSON response from the requested URL
    private String getJsonString(String requestedUrl) {

        // Build new request from requested URL
        Request request = new Request.Builder()
                .url(requestedUrl)
                .build();

        String jsonResponse;  // String contains JSON response

        Response response;   // OkHttp response

        try {
            // Get response from server
            response = mOkHttpClient.newCall(request).execute();
            // Convert response to string
            jsonResponse = response.body().string();
        } catch (IOException e) {
            // In case of error, return empty string
            jsonResponse = "";
        }

        // Shutdown for OkHttp isn't necessary

        return jsonResponse;
    }

    // Parse JSON response from Flickr
    private List<PhotoListItem> parseJsonString(String jsonString) {

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
            // In case of JSONException, do nothing. Empty list will be returned.
        }

        // Return the list of photos
        return items;
    }




}
