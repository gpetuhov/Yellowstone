package com.gpetuhov.android.yellowstone.data;


import android.provider.BaseColumns;


// Defines quake database schema.
// Database has 1 table for storing earthquakes.
public final class YellowstoneContract {

    // Defines constant values for the quakes table
    // BaseColumns already has _id column name defined
    public static final class QuakeEntry implements BaseColumns {

        // Name of the table for quakes
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
