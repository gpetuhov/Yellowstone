package com.gpetuhov.android.yellowstone.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


// Defines quake database schema.
// Database has 1 table for storing earthquakes.
public final class YellowstoneContract {

    // Content authority part of URI to access quake content provider.
    // Every URI consists of three parts: "scheme://content_authority/path".
    public static final String CONTENT_AUTHORITY = "com.gpetuhov.android.yellowstone";

    // Base content URI used to access quake content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path for quake table which will be appended to the base content URI
    public static final String PATH_QUAKES = "quakes";


    // Defines constant values for the quakes table
    // BaseColumns already has _id column name defined
    public static final class QuakeEntry implements BaseColumns {

        // Full content URI to access quake data in the content provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_QUAKES);

        // The MIME type of the URI for a list of quakes
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUAKES;

        // The MIME type of the URL for a single quake
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUAKES;

        // Name of the database table for quakes
        public final static String TABLE_NAME = "quakes";

        // ID of the earthquake in USGS database (comes in JSON response)
        // (this column is different from _id)
        // Type: TEXT
        public final static String COLUMN_IDS = "ids";

        // Magnitude of the earthquake
        // Type: REAL
        public final static String COLUMN_MAGNITUDE = "magnitude";

        // Location name of the earthquake
        // Type: TEXT
        public final static String COLUMN_LOCATION = "location";

        // Latitude of the earthquake
        // Type: REAL
        public final static String COLUMN_LATITUDE = "latitude";

        // Longitude of the earthquake
        // Type: REAL
        public final static String COLUMN_LONGITUDE = "longitude";

        // Depth of the earthquake (in kilometers)
        // Type: REAL
        public final static String COLUMN_DEPTH = "depth";

        // Time of the earthquake (in milliseconds)
        // Type: INTEGER
        public final static String COLUMN_TIME = "time";

        // Website URL of the earthquake
        // Type: TEXT
        public final static String COLUMN_URL = "url";
    }
}
