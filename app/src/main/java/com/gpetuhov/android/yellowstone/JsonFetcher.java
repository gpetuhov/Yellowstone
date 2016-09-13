package com.gpetuhov.android.yellowstone;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


// Fetches JSON string from specified URL
public class JsonFetcher {

    public String getJsonString(String urlSpec) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            // Construct the URL for the query
            URL url = new URL(urlSpec);

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty
                return null;
            }

            jsonStr = buffer.toString();

        } catch (IOException e) {
            // Nothing to return
            Log.e("PlaceholderFragment", "Error ", e);
            return null;

        } finally{
            // Close connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            // Close BufferedReader
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return jsonStr;
    }

}
